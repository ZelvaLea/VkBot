package zelvalea.bot.sdk.request;

import zelvalea.bot.sdk.response.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Request<R extends Response<?>> {
    private final Type respType;
    private final String method;
    private final Map<String, String> params
            = new HashMap<>();

    public Request(String method) {
        ParameterizedType pt = ((ParameterizedType) this.getClass()
                .getGenericSuperclass());
        this.respType = pt.getActualTypeArguments()[0];
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public Type getResponseType() {
        return respType;
    }
    public String toBody() {
        return params.entrySet()
                .stream()
                .map(e -> e.getKey() + '=' + e.getValue())
                .collect(Collectors.joining("&"));
    }
    public void addParam(String name, Object val) {
        params.put(name, URLEncoder.encode(val.toString(), StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
        return method + '=' + toBody();
    }
}
