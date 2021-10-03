package mx.jhcue.poc.kafkaevents.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.jhcue.poc.kafkaevents.avro.MachineEvent;
import mx.jhcue.poc.kafkaevents.config.ApplicationProperties;
import mx.jhcue.poc.kafkaevents.utils.HeadersUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MachineEventKafkaProducer {

    private final KafkaTemplate<String, MachineEvent> kafkaTemplate;
    private final ApplicationProperties applicationProperties;
    private final KafkaProducerCallback<String, MachineEvent> callback;

    public void send(MachineEvent machineEvent, Headers headers) {
        log.info("Sending MachineEvent {} headers {}", machineEvent, HeadersUtils.makeReadable(headers));
        final String key = machineEvent.getMachineId();
        ProducerRecord<String, MachineEvent> producerRecord = new ProducerRecord<>(applicationProperties.getMachineEventTopic(), key, machineEvent);
        headers.forEach(header -> producerRecord.headers().add(header));
        kafkaTemplate.send(producerRecord).addCallback(callback);
        kafkaTemplate.flush();
    }
}
