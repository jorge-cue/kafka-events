package mx.jhcue.poc.kafkaevents.core;

import lombok.extern.slf4j.Slf4j;
import mx.jhcue.poc.kafkaevents.avro.MachineEvent;
import mx.jhcue.poc.kafkaevents.dto.MachineEventDTO;
import mx.jhcue.poc.kafkaevents.mappers.MachineEventMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProcessMachineEventService {
    private final MachineEventMapper mapper = Mappers.getMapper(MachineEventMapper.class);

    public void process(MachineEvent machineEvent) {
        MachineEventDTO machineEventDTO = mapper.avroToDto(machineEvent);
        log.info("Processing machine event {}", machineEventDTO);
    }

}
