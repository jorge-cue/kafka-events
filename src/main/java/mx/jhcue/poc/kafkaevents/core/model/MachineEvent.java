package mx.jhcue.poc.kafkaevents.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.MultiValueMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineEvent {
    private String eventType;
    private Long timestamp;
    private String machineId;
    private String operatorId;
    private MultiValueMap<String, Double> sensors;
}
