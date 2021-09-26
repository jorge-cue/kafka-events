package mx.jhcue.poc.kafkaevents.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

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
        log.info("Success sending message of type {} to topic {}, partition {}, offset {}, timestamp {}\nData: {}",
                producerRecord.value().getClass().getName(),
                metadata.topic(), metadata.partition(), metadata.offset(), ZonedDateTime.ofInstant(Instant.ofEpochSecond(metadata.timestamp()), ZoneOffset.UTC),
                producerRecord.value());
    }
}
