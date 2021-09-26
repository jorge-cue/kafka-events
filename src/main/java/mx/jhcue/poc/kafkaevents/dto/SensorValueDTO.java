package mx.jhcue.poc.kafkaevents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorValueDTO {
    private String sensorId;
    private double value;
}
