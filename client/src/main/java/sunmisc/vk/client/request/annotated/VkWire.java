package sunmisc.vk.client.request.annotated;

import org.apache.commons.text.StringSubstitutor;
import sunmisc.vk.client.Wire;
import sunmisc.vk.client.request.Input;
import sunmisc.vk.client.request.annotations.PathVariable;
import sunmisc.vk.client.request.annotations.RequestProperties;
import sunmisc.vk.client.response.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.RecordComponent;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

public class VkWire implements Wire {
    private static final URI API_URL = URI.create(
            "https://api.vk.com/method/");
    private final Object request;
    private final HttpClient client;

    public VkWire(Object request,
                  HttpClient client) {
        this.request = request;
        this.client = client;
    }


    private Response send(Input input, String method) throws Exception {
        var req = HttpRequest
                .newBuilder(API_URL.resolve(route()))
                .headers(input
                        .headers()
                        .flatMap(x -> Stream.of(x.name(), x.value()))
                        .toArray(String[]::new))
                .method(method, input
                        .body()
                        .map(HttpRequest.BodyPublishers::ofByteArray)
                        .orElse(HttpRequest.BodyPublishers.noBody()))
                .build();

        HttpResponse<byte[]> response =
                client.send(req, HttpResponse.BodyHandlers.ofByteArray());

        return new WrapReponse(response);
    }

    @Override
    public Response patch(Input request) throws Exception {
        return send(request, "PATCH");
    }

    @Override
    public Response post(Input request) throws Exception {
        return send(request, "POST");
    }

    @Override
    public Response delete(Input request) throws Exception {
        return send(request, "DELETE");
    }

    @Override
    public Response get(Input request) throws Exception {
        return send(request, "GET");
    }


    private String route() {
        Map<String, String> variables =
                findComponents(request.getClass(), PathVariable.class)
                        .collect(Collectors.toUnmodifiableMap(
                                x -> x.result().value(),
                                x -> {
                                    Object val = x.apply(request);
                                    return URLEncoder.encode(
                                            requireNonNull(val).toString(),
                                            StandardCharsets.UTF_8);
                                }));
        return StringSubstitutor.replace(
                properties().route(),
                variables,
                "{", "}");
    }

    private RequestProperties properties() {
        return requireNonNull(
                request.getClass().getAnnotation(RequestProperties.class),
                "this request has no properties");
    }
    private <T extends Annotation> Stream<ComponentAccessor<Object,T>>
    findComponents(Class<?> source, Class<T> componentClass) {


        RecordComponent[] components = source.getRecordComponents();
        if (components == null)
            throw new UnsupportedOperationException(
                    "Component queries are only available for record classes");
        return StreamSupport.stream(new ComponentsSpliterator<>(
                components,
                componentClass, 0), false);
    }


    private record WrapReponse(
            HttpResponse<byte[]> response
    ) implements Response {

        @Override
        public int status() {
            return response.statusCode();
        }

        @Override
        public Optional<byte[]> body() {
            return Optional.of(response.body());
        }

        @Override
        public Stream<Header> headers() {
            return response
                    .headers()
                    .map()
                    .entrySet()
                    .stream()
                    .flatMap(x -> {
                        String key = x.getKey();
                        return x
                                .getValue()
                                .stream()
                                .map(r -> new Header(key, r));
                    });
        }
    }
}
