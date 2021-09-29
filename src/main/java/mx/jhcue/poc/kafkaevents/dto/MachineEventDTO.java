package mx.jhcue.poc.kafkaevents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineEventDTO {
    private String eventType;
    private long timestamp;
    private String machineId;
    private String operatorId;
    private List<SensorValueDTO> sensors;
}
