package mx.jhcue.poc.kafkaevents.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MachineEventsKafkaConfig {

    @Bean
    public NewTopic machineEventTopic(ApplicationProperties applicationProperties) {
        return new NewTopic(applicationProperties.getMachineEventTopic(), 1, (short) 1);
    }
}
