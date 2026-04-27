package com.pulsecheck.pulse.monitors;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

public final class Monitor {
	private final String id;
	private final long timeoutSeconds;
	private final String alertEmail;
	private volatile MonitorStatus status;
	private volatile Instant lastHeartbeatAt;
	private volatile ScheduledFuture<?> expiryTask;

	public Monitor(String id, long timeoutSeconds, String alertEmail) {
		this.id = Objects.requireNonNull(id);
		this.timeoutSeconds = timeoutSeconds;
		this.alertEmail = Objects.requireNonNull(alertEmail);
		this.status = MonitorStatus.UP;
		this.lastHeartbeatAt = Instant.now();
	}

	public String getId() {
		return id;
	}

	public long getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public String getAlertEmail() {
		return alertEmail;
	}

	public MonitorStatus getStatus() {
		return status;
	}

	public void setStatus(MonitorStatus status) {
		this.status = status;
	}

	public Instant getLastHeartbeatAt() {
		return lastHeartbeatAt;
	}

	public void setLastHeartbeatAt(Instant lastHeartbeatAt) {
		this.lastHeartbeatAt = lastHeartbeatAt;
	}

	public ScheduledFuture<?> getExpiryTask() {
		return expiryTask;
	}

	public void setExpiryTask(ScheduledFuture<?> expiryTask) {
		this.expiryTask = expiryTask;
	}
}

