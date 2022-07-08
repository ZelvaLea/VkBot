package zelvalea.bot.sdk.request;

import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Request<R> {
    private final Class<R> respType;
    private final String method;
    private final Map<String, String> params
            = new HashMap<>();

    @SuppressWarnings("unchecked")
    public Request(String method) {
        ParameterizedType pt = ((ParameterizedType) this.getClass()
                .getGenericSuperclass());
        this.respType = (Class<R>) pt.getActualTypeArguments()[0];
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public Class<R> getResponseType() {
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
