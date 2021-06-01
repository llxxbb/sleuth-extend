package sleuth.extend;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableAspectJAutoProxy
@AutoConfigureOrder
public class SleuthExtendAutoConfig {
    private final Tracer tracer;
    private final String kafkaTopic;
    private final static Logger logger = LoggerFactory.getLogger(SleuthExtendAutoConfig.class);

    public SleuthExtendAutoConfig(
            Tracer tracer,
            // TODO
            @Value("${spring.zipkin.kafka.topic:zipkin}") String kafkaTopic) {
        this.tracer = tracer;
        this.kafkaTopic = kafkaTopic;
        logger.info("--created--");
    }

    @Bean
    public TracerExtend tracerExtend() {
        return new TracerExtendImpl(tracer);
    }

    @ConditionalOnProperty(value = "spring.zipkin.sender.type", havingValue = "kafka")
    @Bean
    public NewTopic zipkipTopic() {
        return TopicBuilder.name(kafkaTopic)
                .partitions(10)     // 分区数，可提升并发能力。 10为建议值
                .replicas(1)        // 消息的副本， 3为建议值
                .build();
    }

    @Bean
    public SpanExtendAspect spanExtendAspect() {
        return new SpanExtendAspect(tracerExtend(), tracer);
    }

    @Bean
    public TraceParaAspect traceParaAspect() {
        return new TraceParaAspect(tracerExtend(), tracer);
    }
}
