package sunmisc.vk.client.handle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public record ReflectFieldAccessor<T extends Annotation>(
        Field field,
        T property
) implements FieldAccessor<T> {

    @SuppressWarnings("unchecked")
    @Override
    public <R> R get(Object o) {
        try {
            return (R) field.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
