package sleuth.extend;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Tracer;

@Aspect
public class SpanExtendAspect {
    private final static Logger logger = LoggerFactory.getLogger(SpanExtendAspect.class);
    private final TracerExtend tracer;
    private final Tracer sleuth;

    public SpanExtendAspect(TracerExtend tracer, Tracer sleuth) {
        this.tracer = tracer;
        this.sleuth = sleuth;
        logger.info("--created--");
    }

    @Pointcut("@annotation(SpanExtend)")
    private void pointcut() {
    }

    @SuppressWarnings("unused")
    @Around("pointcut() && @annotation(spanExtend) ")
    public Object cutIn(ProceedingJoinPoint pjp, SpanExtend spanExtend) throws Throwable {
        // check
        if (spanExtend == null) return pjp.proceed();

        // append additional parameter
        return SpanInjector.addSpan(
                pjp,
                tracer,
                sleuth,
                spanExtend.value(),
                spanExtend.path(),
                spanExtend.resultTag(),
                spanExtend.exceptionTag()
        );
    }
}
