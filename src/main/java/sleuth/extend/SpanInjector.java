package sleuth.extend;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.internal.SpanNameUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class SpanInjector {

    private final static Logger logger = LoggerFactory.getLogger(SpanInjector.class);

    public static Object addSpan(ProceedingJoinPoint pjp, TracerExtend tracer, Tracer sleuth, String spanName, String[] inTags, String outTag, String exceptionTag) throws Throwable {
        Method method = getMethod(pjp);
        // span name
        String name = spanName == null || spanName.isEmpty() ? method.getName()
                : spanName;
        name = SpanNameUtil.toLowerHyphen(name);
        // create
        Span span = sleuth.nextSpan().name(name);
        try (Tracer.SpanInScope ignored = sleuth.withSpan(span.start())) {
            // add path
            addParams(method, pjp.getArgs(), span, inTags, tracer);
            // execute
            Object proceed = pjp.proceed();
            // add result tag
            if (outTag == null || outTag.isEmpty()) return proceed;
            tracer.addTag(span, outTag, proceed);
            // return
            return proceed;
        } catch (Throwable e) {
            if (exceptionTag == null || exceptionTag.isEmpty()) throw e;
            tracer.addTag(span, exceptionTag, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    static Method getMethod(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        return signature.getMethod();
    }

    public static String[] getInParameters(ProceedingJoinPoint pjp) {
        Method method = getMethod(pjp);
        return Arrays.stream(method.getParameters())
                .map(Parameter::getName)
                .toArray(String[]::new);
    }

    static void addParams(Method method, Object[] args, Span span, String[] paths, TracerExtend tracer) {
        if (paths == null || paths.length == 0) return;
        Parameter[] parameters = method.getParameters();
        for (String path : paths) {
            if (path.isEmpty()) continue;
            String[] parts = path.split("\\.");
            int cnt = 0;
            for (Parameter p : parameters) {
                if (parts[0].equals(p.getName())) {
                    String[] newParts = getNextParts(parts);
                    addTag(newParts, span, path, args[cnt], tracer);
                }
                cnt++;
            }
        }
    }

    static String[] getNextParts(String[] parts) {
        return parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : null;
    }

    static void addTag(String[] parts, Span span, String tag, Object value, TracerExtend tracer) {
        if (parts == null) {
            tracer.addTag(span, tag, value);
            return;
        }
        String part = parts[0];
        if (part.isEmpty()) return;
        Object rtn = getValue(value, part, tag);
        if (rtn == null) return;
        String[] newParts = getNextParts(parts);
        addTag(newParts, span, tag, rtn, tracer);
    }

    static Object getValue(Object value, String property, String path) {
        Class<?> aClass = value.getClass();
        Object rtn = null;
        // get from field
        try {
            Field field = aClass.getField(property);
            rtn = field.get(value);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        if (rtn != null) return rtn;
        // get from method
        String pMethod = "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
        try {
            Method method = aClass.getMethod(pMethod);
            return method.invoke(value);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            logger.warn("not found path: {}", path);
        }
        return null;
    }
}
