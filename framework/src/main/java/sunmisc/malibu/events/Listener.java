package sunmisc.malibu.events;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public interface Listener {

    default Stream<Method> methods() {
        Method[] methods = getClass().getDeclaredMethods();
        return Stream.of(methods);
    }
}
