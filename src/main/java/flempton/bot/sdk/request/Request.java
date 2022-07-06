package flempton.bot.sdk.request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Request<V> {
    private final Class<V> respType;
    private final String method;
    private final Map<String,String> params
            = new HashMap();

    @SuppressWarnings("unchecked")
    public Request(String method) {
        ParameterizedType pt = ((ParameterizedType) this.getClass().getGenericSuperclass());
        this.respType = (Class<V>) pt.getActualTypeArguments()[0];
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
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
    public void addParam(String name, Object val) {
        params.put(name, val.toString());
    }
}
