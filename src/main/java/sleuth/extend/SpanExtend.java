package sleuth.extend;

import java.lang.annotation.*;

/**
 * create span and add result by default
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface SpanExtend {

    /**
     * span name, if not set the method name will be used
     */
    String value() default "";

    /**
     * Example: "user.name",
     * this will find "user" from input parameter first,
     * then will find Field "name", if not found,
     * then will find Method "getName"
     * if found the value will be the tag value, path will be the tag key.
     * if nothing found, ignore it then.
     *
     * Another Example: {"user","user.name"}
     */
    String[] path() default {};

    /**
     * create tag for method result
     */
    String resultTag() default "";

    /**
     * create tag for method exception, the default is "error"
     */
    String exceptionTag() default "error";
}
