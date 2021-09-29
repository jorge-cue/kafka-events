package mx.jhcue.poc.kafkaevents.producer;

import lombok.RequiredArgsConstructor;
import mx.jhcue.poc.kafkaevents.avro.MachineEvent;
import mx.jhcue.poc.kafkaevents.config.ApplicationProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MachineEventKafkaProducer {

    private final KafkaTemplate<String, MachineEvent> kafkaTemplate;
    private final ApplicationProperties applicationProperties;
    private final KafkaProducerCallback<String, MachineEvent> callback;

    public void send(MachineEvent machineEvent) {
        final String key = machineEvent.getMachineId();
        ProducerRecord<String, MachineEvent> producerRecord = new ProducerRecord<>(applicationProperties.getMachineEventTopic(), key, machineEvent);
        kafkaTemplate.send(producerRecord).addCallback(callback);
        kafkaTemplate.flush();
    }
}
