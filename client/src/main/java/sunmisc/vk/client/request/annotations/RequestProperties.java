package sunmisc.vk.client.request.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@RequestMapping
@Target(value=TYPE)
public @interface RequestProperties {

    Method method() default Method.POST;

    String route();

}
