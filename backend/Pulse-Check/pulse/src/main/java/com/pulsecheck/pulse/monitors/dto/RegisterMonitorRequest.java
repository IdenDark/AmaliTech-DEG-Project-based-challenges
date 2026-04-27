package main.java.com.pulsecheck.pulse.monitors.dto;

public record RegisterMonitorRequest(
		String id,
		long timeout,
		String alert_email
) {
}

