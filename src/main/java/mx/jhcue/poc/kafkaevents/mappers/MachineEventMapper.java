package mx.jhcue.poc.kafkaevents.mappers;

import mx.jhcue.poc.kafkaevents.avro.MachineEvent;
import mx.jhcue.poc.kafkaevents.dto.MachineEventDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MachineEventMapper {

    MachineEvent dtoToAvro(MachineEventDTO dto);

    MachineEventDTO avroToDto(MachineEvent avro);
}
