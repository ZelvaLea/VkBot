package sunmisc.vk.client.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class EventHandler {
    private final ReadWriteLock rwl = new ReentrantReadWriteLock();

    private final Map<Class<? extends Event>, EventStack> handlers
            = new IdentityHashMap<>();

    @SuppressWarnings("unchecked")
    public void registerListener(Listener listener) {
        Method[] methods = listener
                .getClass()
                .getDeclaredMethods();

        final Lock w = rwl.writeLock();

        w.lock();
        try {
            for (Method method : methods) {
                method.setAccessible(true);
                if (!method.isAnnotationPresent(EventLabel.class) ||
                        method.getParameterCount() < 1) {
                    continue;
                }
                Class<?> klass = method.getParameterTypes()[0];
                if (!Event.class.isAssignableFrom(klass)) {
                    continue;
                }
                Class<? extends Event> eventType = (Class<? extends Event>) klass;
                EventLabel label = method.getAnnotation(EventLabel.class);

                assert label != null;

                EventConsumer consumer = new EventConsumer(
                        listener,
                        method,
                        label.ignoreCancelled()
                );

                handlers.computeIfAbsent(
                        eventType, k -> new EventStack()
                ).pushEvent(label.priority(), consumer);
            }
        } finally {
            w.unlock();
        }
    }

    public void unregisterListener(Listener listener) {
        final Lock w = rwl.writeLock();

        w.lock();
        try {
            handlers.values().removeIf(v -> {
                v.unregisterListener(listener);

                return v.stack.isEmpty();
            });
        } finally {
            w.unlock();
        }
    }

    public void fire(Event... events) {
        final Lock r = rwl.readLock();

        r.lock();
        try {
            for (Event event : events) {

                EventStack stack = handlers.get(event.getClass());

                if (stack == null) continue;

                stack.tryFire(event);
            }
        } finally {
            r.unlock();
        }

    }

    private static class EventStack {
        private final EnumMap<EventPriority, Collection<EventConsumer>>
                stack = new EnumMap<>(EventPriority.class);

        void pushEvent(
                EventPriority priority,
                EventConsumer consumer) {
            stack.computeIfAbsent(
                    priority, k -> new LinkedList<>()
            ).add(consumer);
        }

        void unregisterListener(Listener listener) {
            stack.values().removeIf(collection -> {

                collection.removeIf(x -> x.source == listener);

                return collection.isEmpty();
            });
        }

        void tryFire(Event event) {
            stack.forEach((k,v) -> v.forEach(e -> e.accept(event)));
        }
    }

    private record EventConsumer(
            Listener source,
            Method invoker,
            boolean ignoreCancelled
    ) implements Consumer<Event> {
        @Override
        public void accept(Event event) {
            if (ignoreCancelled &&
                    event instanceof Cancellable &&
                    (((Cancellable) event).isCancelled()))
                return;
            try {
                invoker.invoke(source, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
