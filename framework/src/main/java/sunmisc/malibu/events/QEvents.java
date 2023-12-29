package sunmisc.malibu.events;

import sunmisc.malibu.events.annotations.EventLabel;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QEvents implements Events {
    private static final MethodHandles.Lookup LOOKUP
            = MethodHandles.lookup();
    private static final Logger LOGGER
            = Logger.getLogger("EventHandler");
    private final ReadWriteLock rwl = new ReentrantReadWriteLock();

    private final Map<Class<? extends Event>, EventStack> handlers
            = new IdentityHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public void subscribe(Listener listener) throws IllegalAccessException {

        Objects.requireNonNull(listener);

        MethodHandles.Lookup lookup = MethodHandles
                .privateLookupIn(listener.getClass(), LOOKUP);

        final Lock w = rwl.writeLock();

        w.lock();
        try {
            listener.methods()
                    .filter(method ->
                            method.isAnnotationPresent(EventLabel.class) &&
                            method.getParameterCount() > 0)
                    .sequential() // ensure
                    .forEach(method -> {
                        Class<?> klass = method.getParameterTypes()[0];
                        if (!Event.class.isAssignableFrom(klass))
                            return;
                        Class<? extends Event> eventType =
                                (Class<? extends Event>) klass;
                        EventLabel label = method.getAnnotation(EventLabel.class);

                        try {
                            MethodHandle handle = lookup.unreflect(method);
                            if (!Modifier.isStatic(method.getModifiers()))
                                handle = handle.bindTo(listener);
                            var consumer = new EventConsumer(handle, label);
                            handlers.computeIfAbsent(
                                    eventType, k -> new EventStack()
                            ).pushEvent(listener, label.priority(), consumer);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }

                    });
        } finally {
            w.unlock();
        }
    }
    @Override
    public void unsubscribe(Listener listener) {
        Objects.requireNonNull(listener);
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
    @Override
    public void fire(Event... events) {
        Objects.requireNonNull(events);

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
        private final EnumMap<EventPriority, Map<Listener, EventConsumer>>
                stack = new EnumMap<>(EventPriority.class);

        void pushEvent(Listener source,
                       EventPriority priority,
                       EventConsumer consumer) {
            stack.computeIfAbsent(
                    priority, k -> new IdentityHashMap<>()
            ).put(source, consumer);
        }

        void unregisterListener(Listener listener) {
            stack.values().removeIf(collection -> {

                collection.remove(listener);

                return collection.isEmpty();
            });
        }

        void tryFire(Event event) {
            stack.forEach((k,v) -> v.forEach((x,y) -> y.accept(event)));
        }
    }

    private record EventConsumer(
            MethodHandle invoker,
            EventLabel label
    ) implements Consumer<Event> {
        @Override
        public void accept(Event event) {
            if (label.ignoreCancelled() &&
                    event instanceof Cancellable cancellable &&
                    cancellable.cancelled())
                return;
            try {
                invoker.invoke(event);
            } catch (Throwable e) {
                LOGGER.log(Level.SEVERE,
                        "Exception occurred while invoking method:", e);
            }
        }
    }
}
