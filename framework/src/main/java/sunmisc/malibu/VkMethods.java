package sunmisc.malibu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import sunmisc.malibu.exceptions.ApiErrorException;
import sunmisc.malibu.model.ApiError;
import sunmisc.malibu.request.VkMethod;
import sunmisc.malibu.request.VkWire;
import sunmisc.malibu.request.annotated.Request;
import sunmisc.malibu.response.Response;

import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.util.Objects;

public class VkMethods implements Methods {
    private static final ObjectMapper OBJECT_MAPPER
            = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }
    private final String token;
    private final HttpClient client;

    public VkMethods(String token, HttpClient client) {
        this.token = token;
        this.client = client;
    }


    @Override
    public <R> R invoke(Request<R> request) throws Exception {
        Objects.requireNonNull(request);

        return handleResponse(
                new VkWire(request.uri(), client)
                        .post(new VkMethod(request, token)),
                request);
    }

    private static <R> R
    handleResponse(Response response,
                   Request<R> request)
            throws Exception {

        int statusCode = response.status();
        if (statusCode == 200) {
            Type type = request.responseType();

            JavaType javaType = OBJECT_MAPPER
                    .getTypeFactory()
                    .constructParametricType(
                            VkResponse.class,
                            OBJECT_MAPPER.constructType(type)
                    );
            VkResponse<R> resp = OBJECT_MAPPER.readValue(
                    response.body().orElseThrow(), javaType);

            ApiError error = resp.error();
            if (error != null)
                throw new ApiErrorException(
                        error.error_msg(),
                        error.error_code());
            return resp.response();
        }
        throw new ApiErrorException("Internal error", statusCode);
    }

    @Override
    public void close() {
        client.close();
    }

    @JsonIgnoreProperties
    public record VkResponse<R>(R response, ApiError error) { }


}
