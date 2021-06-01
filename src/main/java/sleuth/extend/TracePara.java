package sleuth.extend;

import java.lang.annotation.*;

/**
 * create span and capture all input-param and output-para.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface TracePara {

    /**
     * span name, if not set the method name will be used
     */
    String value() default "";

    /**
     * This will append additional Tag to parameters.
     * Example: "user.name",
     * this will find "user" from input parameter first,
     * then will find Field "name", if not found,
     * then will find Method "getName"
     * if found the value will be the tag value, path will be the tag key.
     * if nothing found, ignore it then.
     * You can assign multiple names, for example : {"user.name", "user.id"}
     */
    String[] append() default {};

    /**
     * create tag for method result
     */
    String resultTag() default "result";

    /**
     * create tag for method exception, the default is "error"
     */
    String exceptionTag() default "error";
}
