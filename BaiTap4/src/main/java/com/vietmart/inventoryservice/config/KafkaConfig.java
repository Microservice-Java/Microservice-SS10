package com.vietmart.inventoryservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        // DeadLetterPublishingRecoverer tự động đẩy message thất bại sang Dead Letter Topic (DLT): <topic-name>.DLT
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, (record, exception) -> {
            log.error("Đã vượt quá số lần retry tối đa cho record key: {}, offset: {}. Chuyển message vào DLQ (order-events.DLT)...", record.key(), record.offset());
            return new org.apache.kafka.common.TopicPartition(record.topic() + ".DLT", record.partition());
        });

        // Retry 3 lần: 1 lần xử lý ban đầu + 2 lần retry (FixedBackOff: 1000ms delay, max 2 retries)
        FixedBackOff backOff = new FixedBackOff(1000L, 2L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn("Retry lượt thứ {}/3 cho message tại topic: {}, offset: {} do lỗi: {}",
                        deliveryAttempt, record.topic(), record.offset(), ex.getMessage())
        );

        return errorHandler;
    }
}
