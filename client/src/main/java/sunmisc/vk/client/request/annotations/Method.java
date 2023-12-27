package sunmisc.vk.client.request.annotations;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Method {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE");

    final String name;

    Method(String name) {
        this.name = name;
    }

    public String methodName() {
        return name;
    }

    public static Method resolve(String method) {
        Objects.requireNonNull(method, "Method must not be null");

        return Objects.requireNonNull(lookup.get(method),
                "Method could not be recognized");
    }

    private static final Map<String, Method> lookup;

    static {
        lookup = Arrays.stream(values())
                .collect(Collectors.toUnmodifiableMap(
                        Method::methodName,
                        Function.identity())
                );
    }
}
