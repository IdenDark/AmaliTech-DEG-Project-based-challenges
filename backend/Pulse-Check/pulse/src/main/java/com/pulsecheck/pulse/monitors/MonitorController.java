package main.java.com.pulsecheck.pulse.monitors;

import main.java.com.pulse.bonh.monitors.dto.MessageResponse;
import main.java.com.pulse.bonh.monitors.dto.RegisterMonitorRequest;
import main.java.org.springframework.http.HttpStatus;
import main.java.org.springframework.http.ResponseEntity;
import main.java.org.springframework.web.bind.annotation.GetMapping;
import main.java.org.springframework.web.bind.annotation.PathVariable;
import main.java.org.springframework.web.bind.annotation.PostMapping;
import main.java.org.springframework.web.bind.annotation.RequestBody;
import main.java.org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitorController {
	private final MonitorService monitorService;

	public MonitorController(MonitorService monitorService) {
		this.monitorService = monitorService;
	}

	@PostMapping("/monitors")
	public ResponseEntity<MessageResponse> register(@RequestBody RegisterMonitorRequest req) {
		monitorService.register(req.id(), req.timeout(), req.alert_email());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new MessageResponse("Monitor registered and countdown started."));
	}

	@PostMapping("/monitors/{id}/heartbeat")
	public ResponseEntity<MessageResponse> heartbeat(@PathVariable String id) {
		var monitorOpt = monitorService.heartbeat(id);
		if (monitorOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new MessageResponse("Monitor not found."));
		}

		var monitor = monitorOpt.get();
		if (monitor.getStatus() == MonitorStatus.DOWN) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new MessageResponse("Monitor is down (timer already expired)."));
		}

		return ResponseEntity.ok(new MessageResponse("Heartbeat received. Countdown reset."));
	}
}

