package com.pulsecheck.pulse.monitors;

import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class MonitorService {
	private final Map<String, Monitor> monitors = new ConcurrentHashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public Monitor register(String id, long timeoutSeconds, String alertEmail) {
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("id is required");
		}
		if (timeoutSeconds <= 0) {
			throw new IllegalArgumentException("timeout must be > 0 seconds");
		}
		if (alertEmail == null || alertEmail.isBlank()) {
			throw new IllegalArgumentException("alert_email is required");
		}

		var monitor = new Monitor(id, timeoutSeconds, alertEmail);
		scheduleExpiry(monitor);
		monitors.put(id, monitor);
		return monitor;
	}

	public Optional<Monitor> heartbeat(String id) {
		if (id == null || id.isBlank()) {
			return Optional.empty();
		}

		var monitor = monitors.get(id);
		if (monitor == null) {
			return Optional.empty();
		}

		synchronized (monitor) {
			if (monitor.getStatus() == MonitorStatus.DOWN) {
				return Optional.of(monitor);
			}
			monitor.setLastHeartbeatAt(Instant.now());
			cancelExpiry(monitor.getExpiryTask());
			scheduleExpiry(monitor);
			return Optional.of(monitor);
		}
	}

	private void scheduleExpiry(Monitor monitor) {
		Objects.requireNonNull(monitor);
		ScheduledFuture<?> task = scheduler.schedule(() -> expire(monitor), monitor.getTimeoutSeconds(), TimeUnit.SECONDS);
		monitor.setExpiryTask(task);
	}

	private void expire(Monitor monitor) {
		synchronized (monitor) {
			if (monitor.getStatus() == MonitorStatus.DOWN) {
				return;
			}
			monitor.setStatus(MonitorStatus.DOWN);
			var payload = """
					{"ALERT": "Device %s is down!", "time": "%s"}
					""".formatted(monitor.getId(), Instant.now().toString()).trim();
			System.out.println(payload);
		}
	}

	private void cancelExpiry(ScheduledFuture<?> task) {
		if (task == null) {
			return;
		}
		task.cancel(false);
	}

	@PreDestroy
	void shutdown() {
		scheduler.shutdownNow();
	}
}

