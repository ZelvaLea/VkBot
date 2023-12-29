package sunmisc.malibu.events.annotations;

import sunmisc.malibu.events.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventLabel {

    EventPriority priority() default EventPriority.NORMAL;

    boolean ignoreCancelled() default false;
}
