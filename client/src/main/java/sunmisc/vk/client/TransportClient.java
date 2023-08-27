package sunmisc.vk.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.text.StringSubstitutor;
import sunmisc.vk.client.annotations.ParameterName;
import sunmisc.vk.client.annotations.PathVariable;
import sunmisc.vk.client.annotations.RequestProperties;
import sunmisc.vk.client.exceptions.ApiErrorException;
import sunmisc.vk.client.handle.RequestLookup;
import sunmisc.vk.client.model.ApiError;
import sunmisc.vk.client.request.Request;
import sunmisc.vk.client.response.Response;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class TransportClient implements AutoCloseable {
    private static final String API_VERSION = "5.131";
    private static final String API_URL = "https://api.vk.com/method/";
    private static final AtomicInteger CLIENT_IDS
            = new AtomicInteger();
    private static final RequestLookup REQUEST_LOOKUP
            = new RequestLookup();
    private static final ObjectMapper OBJECT_MAPPER
            = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    private final HttpClient client;
    private final String accessToken;

    public TransportClient(HttpClient client, String accessToken) {
        this.client = client;
        this.accessToken = accessToken;

        CLIENT_IDS.getAndIncrement();
    }

    public <R> R send(Request<R> request) {
        try {
            HttpResponse<String> hr = client.send(
                    createHttpRequest(request),
                    HttpResponse.BodyHandlers.ofString());

            return handleResponse(hr, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <R> CompletableFuture<R> sendAsync(Request<R> request) {
        HttpRequest hr = createHttpRequest(request);
        return client
                .sendAsync(hr, HttpResponse.BodyHandlers.ofString())
                .thenApply(r -> {
                    try {
                        return handleResponse(r, request);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private <R> HttpRequest
    createHttpRequest(Request<R> request) {

        Class<? extends Request> clazz = request.getClass();
        RequestProperties prop = clazz.getAnnotation(RequestProperties.class);

        Objects.requireNonNull(prop, "this request has no properties");

        URI path = URI.create(API_URL + buildUrlPath(request, prop));

        String params = REQUEST_LOOKUP
                .findProperty(clazz, ParameterName.class)
                .stream()
                .map(x -> {
                    ParameterName pn = x.property();
                    String key = pn.value();
                    Object val = x.get(request);

                    return key + '=' + URLEncoder.encode(
                            String.valueOf(val),
                            StandardCharsets.UTF_8);
                })
                .collect(Collectors.joining("&"));
        return HttpRequest
                .newBuilder(path)
                .headers("User-Agent", "VKAndroidApp/7.35")
                .POST(HttpRequest.BodyPublishers.ofString(
                        params + "&access_token="+accessToken+"&v="+API_VERSION
                ))
                .build();
    }
    private static <R> R
    handleResponse(HttpResponse<String> response,
                   Request<R> request)
            throws JsonProcessingException {

        int statusCode = response.statusCode();
        if (statusCode == 200) {
            Type type = request.responseType();

            if (type == Void.class)
                return null;

            JavaType javaType = OBJECT_MAPPER
                    .getTypeFactory()
                    .constructParametricType(
                            Response.class,
                            OBJECT_MAPPER.constructType(type)
                    );
            Response<R> resp = OBJECT_MAPPER.readValue(
                    response.body(), javaType);

            ApiError error = resp.error();
            if (error != null)
                throw new ApiErrorException(
                        error.error_msg(),
                        error.error_code());
            return resp.response();
        }
        throw new ApiErrorException("Internal error", statusCode);
    }

    private static <R> String
    buildUrlPath(Request<R> request, RequestProperties prop) {

        Class<? extends Request> clazz = request.getClass();

        Map<String, String> variables = REQUEST_LOOKUP
                .findProperty(clazz, PathVariable.class)
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        x -> {
                            PathVariable pv = x.property();
                            return pv.value();
                        },
                        x -> {
                            Object val = x.get(request);
                            return URLEncoder.encode(
                                    String.valueOf(val),
                                    StandardCharsets.UTF_8);
                        }));
        // todo: StringTemplate

        return StringSubstitutor.replace(
                prop.route(),
                variables,
                "{", "}");
    }

    @Override
    public void close() throws Exception {
        // todo: close httpclient in jdk 21

        if (CLIENT_IDS.decrementAndGet() == 0)
            REQUEST_LOOKUP.clear();
    }
}
