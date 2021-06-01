package sleuth.extend;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Tracer;

@Aspect
public class TraceParaAspect {
    private final static Logger logger = LoggerFactory.getLogger(TraceParaAspect.class);
    private final TracerExtend tracer;
    private final Tracer sleuth;

    public TraceParaAspect(TracerExtend tracer, Tracer sleuth) {
        this.tracer = tracer;
        this.sleuth = sleuth;
        logger.info("--created--");
    }

    @Pointcut("@annotation(TracePara)")
    private void pointcut() {
    }

    @SuppressWarnings("unused")
    @Around("pointcut() && @annotation(tracePara) ")
    public Object cutIn(ProceedingJoinPoint pjp, TracePara tracePara) throws Throwable {
        // check
        if (tracePara == null) return pjp.proceed();
        // get all input parameter
        String[] inTags = mergeTags(SpanInjector.getInParameters(pjp), tracePara.append());
        // append additional parameter
        return SpanInjector.addSpan(
                pjp,
                tracer,
                sleuth,
                tracePara.value(),
                inTags,
                tracePara.resultTag(),
                tracePara.exceptionTag()
        );
    }

    static String[] mergeTags(String[] inParameters, String[] append) {
        String[] inTags = new String[inParameters.length + append.length];
        System.arraycopy(inParameters, 0, inTags, 0, inParameters.length);
        System.arraycopy(append, 0, inTags, inParameters.length, append.length);
        return inTags;
    }
}
