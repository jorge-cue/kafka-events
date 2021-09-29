package mx.jhcue.poc.kafkaevents.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.jhcue.poc.kafkaevents.avro.MachineEvent;
import mx.jhcue.poc.kafkaevents.core.ProcessMachineEventService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class MachineEventKafkaListener {
    private final ProcessMachineEventService processMachineEventService;

    @KafkaListener(topics = "${application.machine-event-topic}")
    public void receive(ConsumerRecord<String, MachineEvent> consumerRecord) {
        log.info("Receiving machine event from topic {}, partition {}, offset {}, timestamp {}\n>Key {}\nData {}",
                consumerRecord.topic(),consumerRecord.partition(), consumerRecord.offset(),
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(consumerRecord.timestamp()), ZoneOffset.UTC),
                consumerRecord.key(),
                consumerRecord.value());
        processMachineEventService.process(consumerRecord.value());
    }
}
