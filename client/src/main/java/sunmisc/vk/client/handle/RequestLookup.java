package sunmisc.vk.client.handle;

import sunmisc.vk.client.annotations.RequestMapping;
import sunmisc.vk.client.request.Request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class RequestLookup {

    private static final int IGNORED_FIELDS_MASK =
            Modifier.TRANSIENT |
                    Modifier.STATIC;


    /*
     * don't need a concurrent impl,
     * so the race won't break anything for us
     * but because of modCount it throws
     * an exception ConcurrentModificationException
     * */
    private final Map<Class<? extends Request>, PropertyList> lookup
            = new ConcurrentHashMap<>(); // new IdentityHashMap<>();

    private static boolean checkValidField(Field field) {
        return (field.getModifiers() & IGNORED_FIELDS_MASK) == 0;
    }


    public <T extends Annotation> List<FieldAccessor<T>>
    findProperty(Class<? extends Request> request, Class<T> propClass) {

        return lookup.computeIfAbsent(request, k -> {

            Map<Class<? extends Annotation>, List<FieldAccessor<?>>>
                    map = new IdentityHashMap<>();

            for (Field field : request.getDeclaredFields()) {
                if (checkValidField(field)) {

                    field.setAccessible(true);

                    for (Annotation annotation : field.getAnnotations()) {

                        Class<? extends Annotation> tp
                                = annotation.annotationType();

                        if (!tp.isAnnotationPresent(RequestMapping.class))
                            continue;

                        ReflectFieldAccessor<?> fi =
                                new ReflectFieldAccessor<>(field, annotation);


                        map.computeIfAbsent(tp,
                                r -> new LinkedList<>()
                        ).add(fi);
                    }
                }
            }
            // reduce weight of the map and make it immutable
            return new PropertyList(map
                    .entrySet()
                    .stream()
                    .collect(Collectors.toUnmodifiableMap(
                            Map.Entry::getKey,
                            x -> List.copyOf(x.getValue()))
                    ));
        }).findField(propClass);
    }
    public void clear() {
        lookup.clear();
    }

    private record PropertyList(
            Map<Class<? extends Annotation>, List<? extends FieldAccessor<?>>> fields) {

        @SuppressWarnings("unchecked")
        <T extends Annotation> List<FieldAccessor<T>>
        findField(Class<T> clazz) {
            return (List<FieldAccessor<T>>) fields.getOrDefault(
                    clazz,
                    Collections.emptyList());
        }
    }
}
