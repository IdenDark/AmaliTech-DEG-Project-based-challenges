package com.pulsecheck.pulse.monitors;

import com.pulsecheck.pulse.monitors.dto.MessageResponse;
import com.pulsecheck.pulse.monitors.dto.RegisterMonitorRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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


	/////endpoint for snoozing...///////
	@PostMapping("/monitors/{id}/snooze")
	public ResponseEntity<MessageResponse> snooze(@PathVariable String id) {
		var result = monitorService.snoozeMonitor(id);
		if (result.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new MessageResponse("Monitor not found."));
		}
		return ResponseEntity.ok(new MessageResponse("Monitor snoozed. Timer stopped."));
	}

	/// endpont for getting monitor data.

	@GetMapping("/monitors/{id}")
	public ResponseEntity<?> getMonitor(@PathVariable String id) {
		try {
			var monitor = monitorService.getMonitor(id);
			return ResponseEntity.ok(monitor);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new MessageResponse(e.getMessage()));
		}
	}

}

