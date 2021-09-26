package mx.jhcue.poc.kafkaevents.core;

import lombok.RequiredArgsConstructor;
import mx.jhcue.poc.kafkaevents.dto.MachineEventDTO;
import mx.jhcue.poc.kafkaevents.mappers.MachineEventMapper;
import mx.jhcue.poc.kafkaevents.sender.MachineEventKafkaProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AcceptMachineEventService {
    private final MachineEventKafkaProducer producer;
    private final MachineEventMapper mapper;

    public void apply(MachineEventDTO machineEvent) {
        var avro = mapper.dtoToAvro(machineEvent);
        producer.accept(avro);
    }
}
