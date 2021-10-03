package mx.jhcue.poc.kafkaevents.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.jhcue.poc.kafkaevents.dto.MachineEventDTO;
import mx.jhcue.poc.kafkaevents.mappers.MachineEventMapper;
import mx.jhcue.poc.kafkaevents.producer.MachineEventKafkaProducer;
import mx.jhcue.poc.kafkaevents.utils.HeadersUtils;
import org.apache.kafka.common.header.Headers;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcceptMachineEventService {
    private final MachineEventKafkaProducer producer;
    private final MachineEventMapper mapper;

    public void accept(MachineEventDTO machineEvent, HttpHeaders httpHeaders) {
        log.info("Accepting machineEvent {} with headers {}", machineEvent, httpHeaders);
        var avro = mapper.dtoToAvro(machineEvent);
        Headers kafkaHeaders = HeadersUtils.mapHttpHeadersToHeaders(httpHeaders);
        if (!kafkaHeaders.headers(KafkaHeaders.CORRELATION_ID).iterator().hasNext()) {
            kafkaHeaders.add(KafkaHeaders.CORRELATION_ID, machineEvent.getMachineId().getBytes(StandardCharsets.UTF_8));
        }
        producer.send(avro, kafkaHeaders);
    }
}
