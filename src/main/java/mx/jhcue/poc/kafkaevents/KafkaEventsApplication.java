package mx.jhcue.poc.kafkaevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class KafkaEventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaEventsApplication.class, args);
    }

}
