package mx.jhcue.poc.kafkaevents.kafka;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import mx.jhcue.poc.kafkaevents.avro.EventTypeEnum;
import mx.jhcue.poc.kafkaevents.avro.MachineEvent;
import mx.jhcue.poc.kafkaevents.avro.SensorValue;
import mx.jhcue.poc.kafkaevents.listener.MachineEventKafkaListener;
import mx.jhcue.poc.kafkaevents.producer.MachineEventKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.ParameterizedTest.INDEX_PLACEHOLDER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}, topics = {"${application.machine-event-topic}"})
class EmbeddedKafkaIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedKafkaIntegrationTest.class);

    @Autowired
    MachineEventKafkaProducer machineEventKafkaProducer;

    @SpyBean
    MachineEventKafkaListener machineEventKafkaListener;

    @Captor
    ArgumentCaptor<ConsumerRecord<String, MachineEvent>> consumerRecordArgumentCaptor;

    private CountDownLatch latch;

    @BeforeEach
    void setUp() {
        latch = new CountDownLatch(1);
        doAnswer(invoke -> {
            final var consumerRecord = invoke.getArgument(0, ConsumerRecord.class);
            log.info("Spy received record type {}\nKey: {}\nHeaders: {}\nData: {}"
                    , consumerRecord.value().getClass().getName()
                    , consumerRecord.key()
                    , StreamSupport.stream(consumerRecord.headers().spliterator(), false)
                            .collect(Collectors.toMap(Header::key, it -> new String(it.value())))
                    , consumerRecord.value());
            latch.countDown();
            return null;
        }).when(machineEventKafkaListener).receive(any());
    }

    @ParameterizedTest(name = INDEX_PLACEHOLDER + ": {0}")
    @MethodSource("machineEventStream")
    void produceListenMachineEvent(final String displayName, final MachineEvent actual, Headers headers) throws Exception {
        log.info("Executing test {}", displayName);
        machineEventKafkaProducer.send(actual, headers);
        final var receivedOnTime = latch.await(3, TimeUnit.SECONDS);

        verify(machineEventKafkaListener).receive(consumerRecordArgumentCaptor.capture());
        final var consumerRecord = consumerRecordArgumentCaptor.getValue();
        final var received = consumerRecord.value();
        final var correlationId = new String(consumerRecord.headers().headers(KafkaHeaders.CORRELATION_ID).iterator().next().value());

        assertAll(
                () -> assertTrue(receivedOnTime, "Message was received at listener on time"),
                () -> assertEquals(actual.getClass(), received.getClass(), "Sent and Received are of same class"),
                () -> assertEquals(actual, received, "Sent and received records are equal"),
                () -> assertEquals(actual.getMachineId(), correlationId, "Correlation id is carried-on successfully")
        );
    }

    private static Stream<Arguments> machineEventStream() {
        final String machineId = UUID.randomUUID().toString();
        final String operatorId = UUID.randomUUID().toString();
        final String sensor1Id = UUID.randomUUID().toString();
        final String sensor2Id = UUID.randomUUID().toString();

        return Stream.of(
                Arguments.of("MachineEvent START Event without sensors",
                        MachineEvent.newBuilder()
                                .setEventType(EventTypeEnum.START)
                                .setTimestamp(System.currentTimeMillis())
                                .setMachineId(machineId)
                                .setOperatorId(operatorId)
                                .setSensors(List.of())
                                .build(),
                        buildHeaders(machineId)),
                Arguments.of("MachineEvent STATUS Event with sensors values",
                        MachineEvent.newBuilder()
                                .setEventType(EventTypeEnum.STATUS)
                                .setTimestamp(System.currentTimeMillis())
                                .setMachineId(machineId)
                                .setOperatorId(operatorId)
                                .setSensors(List.of(
                                        SensorValue.newBuilder().setSensorId(sensor1Id).setValue(1.0).build(),
                                        SensorValue.newBuilder().setSensorId(sensor2Id).setValue(0.8).build()
                                ))
                                .build(),
                        buildHeaders(machineId)),
                Arguments.of("MachineEvent BREAK Event with sensors",
                        MachineEvent.newBuilder()
                                .setEventType(EventTypeEnum.BREAK)
                                .setTimestamp(System.currentTimeMillis())
                                .setMachineId(machineId)
                                .setOperatorId(operatorId)
                                .setSensors(List.of(
                                        SensorValue.newBuilder().setSensorId(sensor1Id).setValue(-1.0).build(),
                                        SensorValue.newBuilder().setSensorId(sensor2Id).setValue(0.3).build()
                                ))
                                .build(),
                        buildHeaders(machineId)),
                Arguments.of("MachineEvent RESUME Event with sensors",
                        MachineEvent.newBuilder()
                                .setEventType(EventTypeEnum.RESUME)
                                .setTimestamp(System.currentTimeMillis())
                                .setMachineId(machineId)
                                .setOperatorId(operatorId)
                                .setSensors(List.of(
                                        SensorValue.newBuilder().setSensorId(sensor1Id).setValue(0.8).build(),
                                        SensorValue.newBuilder().setSensorId(sensor2Id).setValue(0.9).build()
                                ))
                                .build(),
                        buildHeaders(machineId)),
                Arguments.of("MachineEvent STOP Event without sensors",
                        MachineEvent.newBuilder()
                                .setEventType(EventTypeEnum.STOP)
                                .setTimestamp(System.currentTimeMillis())
                                .setMachineId(machineId)
                                .setOperatorId(operatorId)
                                .setSensors(List.of())
                                .build(),
                        buildHeaders(machineId))

                );
    }

    private static Headers buildHeaders(String correlationId) {
        Headers headers = new RecordHeaders();
        headers.add(KafkaHeaders.CORRELATION_ID, correlationId.getBytes(StandardCharsets.UTF_8));
        headers.add(KafkaHeaders.TIMESTAMP, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME).getBytes(StandardCharsets.UTF_8));
        headers.add(KafkaHeaders.TIMESTAMP_TYPE, "ISO_OFFSET_DATE_TIME".getBytes(StandardCharsets.UTF_8));
        return headers;
    }

    @TestConfiguration
    static class EmbeddedKafkaConfiguration {

        /**
         * Mock Avro Schema Registry Client, this provide schema registry between Provider's Value Serializer and
         * Listener's Value Deserializer.
         *
         * @return The MockSchemaRegistryClient instance. It is not configurable. Available using mock://id URL.
         */
        @Bean
        public SchemaRegistryClient schemaRegistryClient() {
            return new MockSchemaRegistryClient();
        }
    }
}
