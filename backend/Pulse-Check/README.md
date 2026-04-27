

# CritMon - Device Monitoring System

A lightweight device monitoring system built with **Java Spring Boot** that tracks device heartbeats and sends alerts when devices go down.

## Architecture Overview

The system uses a sequence-based architecture where devices register, send heartbeats, and trigger alerts on timeout.

### Sequence Diagram



#### Setup Instructions

-Prerequisites
Java 17 or later (I used v26)
Gradle (or use Gradle Wrapper)

# 1. Clone the repository
git clone https://github.com/IdenDark/AmaliTech-DEG-Project-based-challenges.git
cd backend\Pulse-Check\pulse

# 2. Build the project
./gradlew build

# 3. Run the application
./gradlew bootRun

The server starts at http://localhost:8080

##### API Documentation

Base URL
http://localhost:8080/monitors

# Endpoints
| Method | Endpoint        | Description        |
|--------|-----------------|--------------------|
| POST   | /               | Register a monitor |
| POST   | /{id}/heartbeat | Send heartbeat     |
| POST   | /{id}/snooze    | Pause monitoring   |
| GET    | /{id}           | Get monitor info   |
---

###### USAGE INSTRUCTION.

N.B: KEEP THE SERVER RUNNING IN ONE TERMINAL AND PASTE THE BASHES IN NEW TERMINAL

# Test Step 1.1: Register Monitor
**Goal**: Creating a device

```bash
curl -X POST http://localhost:8080/monitors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "device-123",
    "timeout": 60,
    "alert_email": "admin@critmon.com"
  }'
```
**Expected Response**: 
- Status: `201 Created`
- Body: `{"message":"Monitor registered and countdown started."}`

---

## User Story 2: The Heartbeat

**Goal**: Send heartbeat to reset timer before expiration.

### Test Step 2.1: Send Heartbeat (Within 60 seconds)
```bash
curl -X POST http://localhost:8080/monitors/device-123/heartbeat \
  -H "Content-Type: application/json"
```

**Expected Response**: 
- Status: `200 OK`
- Body: `{"message":"Heartbeat received. Countdown reset."}`

---

### Test Step 2.2: Send Heartbeat to Non-Existent Monitor
```bash
curl -X POST http://localhost:8080/monitors/hoax/heartbeat \
  -H "Content-Type: application/json"
```

**Expected Response**: 
- Status: `404 Not Found`
- Body: `{"message":"Monitor not found."}`

---



### Test Step 2.3: Send Heartbeat After Timer Expires
```bash

# Wait 61+ seconds, then:

curl -X POST http://localhost:8080/monitors/device-123/heartbeat \
  -H "Content-Type: application/json"
```

**Expected Response**: 
- Status: `409 Conflict`
- Body: `{"message":"Monitor is down (timer already expired)."}`

---

## Test Step 2.4: Alarm System

When a timer reaches zero:

Status changes to down

JSON alert logged to console

{"ALERT": "Device device-123 is down!", "time": "2026-04-27T10:00:00Z"}

###### Bonus

```bash

curl -X POST http://localhost:8080/monitors/device-123/snooze \
  -H "Content-Type: application/json"

```

**Expected Response**: 
- Body: `{""OK:200  Monitor snoozed" ."}`
---

###### DEVELOPER'S CHOICE

Why this feature? This endpoint provides complete visibility into device health, enabling dashboards, automation, and troubleshooting.

```bash

curl -X POST http://localhost:8080/monitors/device-123 \
  -H "Content-Type: application/json"
```
  **Expected Response**: 
- Body: `{""OK:200  Monitor snoozed" ."}`
---