package mx.jhcue.poc.kafkaevents.sender;

import lombok.RequiredArgsConstructor;
import mx.jhcue.poc.kafkaevents.avro.MachineEvent;
import mx.jhcue.poc.kafkaevents.config.ApplicationProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.actuate.context.properties.ConfigurationPropertiesReportEndpoint;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class MachineEventKafkaProducer implements Consumer<MachineEvent> {

    private final KafkaTemplate<String, MachineEvent> kafkaTemplate;
    private final ApplicationProperties applicationProperties;
    private final KafkaProducerCallback<String, MachineEvent> callback;

    @Override
    public void accept(MachineEvent machineEvent) {
        ProducerRecord<String, MachineEvent> producerRecord = new ProducerRecord<>(applicationProperties.getMachineEventTopic(), machineEvent);
        kafkaTemplate.send(producerRecord).addCallback(callback);
    }
}
