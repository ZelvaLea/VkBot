package sunmisc.vk.client.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Retention(RetentionPolicy.RUNTIME)
@RequestMapping
@Documented
@Target(value=FIELD)
public @interface PathVariable {

    String value();
}