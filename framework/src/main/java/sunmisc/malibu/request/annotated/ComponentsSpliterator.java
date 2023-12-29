package sunmisc.malibu.request.annotated;


import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Spliterator;
import java.util.function.Consumer;

class ComponentsSpliterator<R, T extends Annotation>
        implements Spliterator<ComponentAccessor<R, T>> {
    private final RecordComponent[] components;
    private final Class<T> property;
    private int cursor;

    ComponentsSpliterator(RecordComponent[] components,
                          Class<T> property,
                          int origin) {
        this.components = components;
        this.property = property;
        this.cursor = origin;
    }


    @Override
    public boolean tryAdvance(Consumer<? super ComponentAccessor<R, T>> action) {
        int origin = cursor, fence = components.length;
        if (origin < fence) {
            try {
                do {
                    RecordComponent component = components[origin++];
                    final Method method = component.getAccessor();
                    final T tp = component.getAnnotation(property);
                    if (tp != null && method.trySetAccessible()) {
                        action.accept(new ComponentAccessor<>() {
                            @Override public T result() { return tp; }

                            @Override
                            public Object apply(R r) {
                                try {
                                    return method.invoke(r);
                                } catch (IllegalAccessException |
                                         InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        return true;
                    }
                } while (origin < fence);
            } finally {
                cursor = origin;
            }
        }
        return false;
    }

    @Override
    public Spliterator<ComponentAccessor<R, T>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return components.length - cursor;
    }

    @Override
    public int characteristics() {
        return IMMUTABLE | NONNULL;
    }
}
