package sunmisc.malibu.request.annotated;

import org.apache.commons.text.StringSubstitutor;
import sunmisc.malibu.request.Input;
import sunmisc.malibu.request.VkHead;
import sunmisc.malibu.request.annotations.ParameterName;
import sunmisc.malibu.request.annotations.PathVariable;
import sunmisc.malibu.request.annotations.RequestProperties;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public interface Request<R> extends Input, VkHead {


    default String uri() {
        Map<String, String> variables =
                findComponents(getClass(), PathVariable.class)
                .collect(Collectors.toUnmodifiableMap(
                        x -> x.result().value(),
                        x -> {
                            Object val = x.apply(this);
                            return URLEncoder.encode(
                                    requireNonNull(val).toString(),
                                    StandardCharsets.UTF_8);
                        }));
        return StringSubstitutor.replace(
                properties().route(),
                variables,
                "{", "}");
    }

    @Override
    default Optional<byte[]> body() {
        var request = getClass();
        return Optional.of(findComponents(request, ParameterName.class)
                .map(x -> {
                    ParameterName pn = x.result();
                    String key = pn.value();
                    Object val = x.apply(this);

                    return STR."\{key}=\{URLEncoder.encode(
                            requireNonNull(val).toString(),
                            StandardCharsets.UTF_8)}";
                })
                .collect(joining("&"))
                .getBytes(StandardCharsets.UTF_8));
    }

    default Type responseType() {
        for (Class<?> member : getClass().getNestMembers()) {
            for (Type t : member.getGenericInterfaces()) {
                if (t instanceof ParameterizedType pt &&
                        pt.getRawType() == Request.class)
                    return pt.getActualTypeArguments()[0];
            }
        }
        throw new InternalError("response type not found");
    }
    private RequestProperties properties() {
        return requireNonNull(
                getClass().getAnnotation(RequestProperties.class),
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

}
