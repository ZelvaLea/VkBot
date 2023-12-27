package sunmisc.vk.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import sunmisc.vk.client.exceptions.ApiErrorException;
import sunmisc.vk.client.model.ApiError;
import sunmisc.vk.client.request.VkMethod;
import sunmisc.vk.client.request.annotated.Request;
import sunmisc.vk.client.request.annotated.VkWire;
import sunmisc.vk.client.response.Response;

import java.lang.reflect.Type;
import java.net.http.HttpClient;

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
    public <R> R send(Request<R> request) throws Exception {

        var method = new VkMethod(request, token);
        Response input = new VkWire(request, client).post(method);

        return handleResponse(input, request);
    }

    private static <R> R
    handleResponse(Response response,
                   Request<R> request)
            throws Exception {

        int statusCode = response.status();
        if (statusCode == 200) {
            Type type = request.responseType();

            if (type == Void.class)
                return null;

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

    @JsonIgnoreProperties
    public record VkResponse<R>(R response, ApiError error) { }


    @Override
    public void close() throws Exception {

    }
}
