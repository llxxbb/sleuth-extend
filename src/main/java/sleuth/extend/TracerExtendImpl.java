package sleuth.extend;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

public class TracerExtendImpl implements TracerExtend {
    private final Tracer sleuthTracer;
    private final ObjectMapper mapper;
    private final static Logger logger = LoggerFactory.getLogger(TracerExtendImpl.class);

    public TracerExtendImpl(Tracer sleuthTracer) {
        this.sleuthTracer = sleuthTracer;
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        logger.info("--created--");
    }

    @Override
    public void addTag(String tag, Object data) {
        if (data == null) return;
        Span span = sleuthTracer.currentSpan();
        if (span == null) return;
        addTag(tag, data, span);
    }

    @Override
    public void addTag(Span span, String tag, Object data) {
        if (data == null || tag == null || span == null) return;
        addTag(tag, data, span);
    }

    private void addTag(String tag, Object data, Span span) {
        try {
            String json = data instanceof String ? (String) data : mapper.writeValueAsString(data);
            span.tag(tag, json);
        } catch (JsonProcessingException e) {
            logger.warn("convert data to json error");
        }
    }
}
