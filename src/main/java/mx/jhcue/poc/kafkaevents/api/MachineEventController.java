package mx.jhcue.poc.kafkaevents.api;

import lombok.RequiredArgsConstructor;
import mx.jhcue.poc.kafkaevents.core.AcceptMachineEventService;
import mx.jhcue.poc.kafkaevents.dto.MachineEventDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/machine/events")
@RequiredArgsConstructor
public class MachineEventController {
    private final AcceptMachineEventService acceptMachineEventService;

    @PostMapping
    public ResponseEntity<Void> registerEvent(@Validated @RequestBody MachineEventDTO body) {
        acceptMachineEventService.apply(body);
        return ResponseEntity.accepted().build();
    }
}
