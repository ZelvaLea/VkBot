package sunmisc.vk.client.request.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.RECORD_COMPONENT;

@Retention(RetentionPolicy.RUNTIME)
@RequestMapping
@Target(RECORD_COMPONENT)
public @interface PathVariable {
    String value();
}