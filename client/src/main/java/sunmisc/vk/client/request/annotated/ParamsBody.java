package sunmisc.vk.client.request.annotated;

import sunmisc.vk.client.request.Body;
import sunmisc.vk.client.request.annotations.ParameterName;

import java.lang.annotation.Annotation;
import java.lang.reflect.RecordComponent;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class ParamsBody implements Body {

    private final Object record;

    public ParamsBody(Object record) {
        this.record = record;
    }

    @Override
    public Optional<byte[]> body() {
        var request = record.getClass();
        return Optional.of(findComponents(request, ParameterName.class)
                .map(x -> {
                    ParameterName pn = x.result();
                    String key = pn.value();
                    Object val = x.apply(record);

                    return STR."\{key}=\{URLEncoder.encode(
                            requireNonNull(val).toString(),
                            StandardCharsets.UTF_8)}";
                })
                .collect(joining("&"))
                .getBytes(StandardCharsets.UTF_8));
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
