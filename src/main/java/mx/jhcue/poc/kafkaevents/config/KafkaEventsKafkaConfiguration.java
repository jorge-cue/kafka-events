package mx.jhcue.poc.kafkaevents.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaEventsKafkaConfiguration {
    /**
     *
     * @param kafkaProperties
     * @return
     */
    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProperties kafkaProperties) {
        Map<String, Object> configs = new HashMap<>(kafkaProperties.buildAdminProperties());
        return new KafkaAdmin(configs);
    }

    /**
     *
     * @return
     */
    @Bean
    public NewTopic newTopicCmdProofOfConcept() {
        return new NewTopic("cmd_proof_of_concept", 1, (short)1);
    }

    /**
     *
     * @param kafkaProperties
     * @param <K>
     * @param <V>
     * @return
     */
    @Bean
    public <K, V> ProducerFactory<K, V> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties());
        return new DefaultKafkaProducerFactory<>(properties);
    }

    /**
     *
     * @param producerFactory
     * @param <K>
     * @param <V>
     * @return
     */
    @Bean
    public <K, V> KafkaTemplate<K, V> kafkaTemplate(ProducerFactory<K, V> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     *
     * @param kafkaProperties
     * @param <K>
     * @param <V>
     * @return
     */
    @Bean
    public <K, V> ConsumerFactory<K, V> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties());
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    /**
     *
     * @param consumerFactory
     * @param <K>
     * @param <V>
     * @return
     */
    @Bean
    public <K, V> MessageListenerContainer messageListenerContainer(ConsumerFactory<K, V> consumerFactory) {
        ContainerProperties containerProperties = new ContainerProperties("cmd_proof_of_concept");
        containerProperties.setAckMode(ContainerProperties.AckMode.RECORD);
        containerProperties.setMicrometerEnabled(true);
        return new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
    }
}
