package sleuth.extend;

import org.springframework.cloud.sleuth.Span;

public interface TracerExtend {
    void addTag(String tag, Object data);

    void addTag(Span span, String tag, Object data);
}
