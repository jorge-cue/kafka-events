package mx.jhcue.poc.kafkaevents.mappers;

import mx.jhcue.poc.kafkaevents.avro.EventTypeEnum;
import mx.jhcue.poc.kafkaevents.avro.MachineEvent;
import mx.jhcue.poc.kafkaevents.avro.SensorValue;
import mx.jhcue.poc.kafkaevents.dto.MachineEventDTO;
import mx.jhcue.poc.kafkaevents.dto.SensorValueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MachineEventMapperTest {

    MachineEventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MachineEventMapper.class);
    }

    @Test
    void mapDtoToAvro() {
        // THis Machine is a
        final MachineEventDTO dto = MachineEventDTO.builder()
                .eventType("START")
                .timestamp(System.currentTimeMillis())
                .machineId(UUID.randomUUID().toString())
                .operatorId(UUID.randomUUID().toString())
                .sensors(List.of(
                        SensorValueDTO.builder()
                                .sensorId("soapWeight")
                                .value(1.0)
                                .build()
                ))
                .build();

        final var avro = mapper.dtoToAvro(dto);

        assertAll(
                () -> assertNotNull(avro),
                () -> assertNotNull(dto),
                () -> assertEquals(dto.getEventType(), avro.getEventType().name()),
                () -> assertEquals(dto.getMachineId(), avro.getMachineId()),
                () -> assertEquals(dto.getOperatorId(), avro.getOperatorId()),
                () -> assertEquals(dto.getTimestamp(), avro.getTimestamp()),
                () -> assertEquals(dto.getSensors().size(), avro.getSensors().size())
        );
    }

    @Test
    void mapsAvroToDto() {
        final MachineEvent avro = MachineEvent.newBuilder()
                .setEventType(EventTypeEnum.START)
                .setMachineId(UUID.randomUUID().toString())
                .setOperatorId(UUID.randomUUID().toString())
                .setTimestamp(System.currentTimeMillis())
                .setSensors(List.of(
                        SensorValue.newBuilder()
                                .setSensorId("soapWeight")
                                .setValue(1.0)
                                .build()
                ))
                .build();

        final var dto = mapper.avroToDto(avro);

        assertAll(
                () -> assertNotNull(avro),
                () -> assertNotNull(dto),
                () -> assertEquals(avro.getEventType().name(), dto.getEventType()),
                () -> assertEquals(avro.getMachineId(), dto.getMachineId()),
                () -> assertEquals(avro.getOperatorId(), dto.getOperatorId()),
                () -> assertEquals(avro.getTimestamp(), dto.getTimestamp()),
                () -> assertEquals(avro.getSensors().size(), dto.getSensors().size())
        );
    }
}