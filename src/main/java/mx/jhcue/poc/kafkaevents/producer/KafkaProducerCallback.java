package mx.jhcue.poc.kafkaevents.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class KafkaProducerCallback<K, V> implements ListenableFutureCallback<SendResult<K, V>> {

    @Override
    public void onFailure(Throwable ex) {
        log.error("Error sending to kafka {}", ex.getMessage(), ex);
    }

    @Override
    public void onSuccess(SendResult<K, V> result) {
        if (result == null) return;
        var metadata = result.getRecordMetadata();
        var producerRecord = result.getProducerRecord();
        log.info("Success sending message of type {} to topic {}, partition {}, offset {}, timestamp {}\nKey: {}\nHeaders: {}\nData: {}",
                producerRecord.value().getClass().getName(),
                metadata.topic(), metadata.partition(), metadata.offset(), ZonedDateTime.ofInstant(Instant.ofEpochSecond(metadata.timestamp()), ZoneOffset.UTC),
                producerRecord.key(),
                visibleHeaders(producerRecord.headers()),
                producerRecord.value());
    }

    private MultiValueMap<String, String> visibleHeaders(Headers headers) {
        MultiValueMap<String, String> visibleHeaders = new LinkedMultiValueMap<>();
        headers.forEach(header -> visibleHeaders.add(header.key(), new String(header.value(), StandardCharsets.UTF_8)));
        return visibleHeaders;
    }
}
