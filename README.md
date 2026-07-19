# Java Circuit Breaker Implementation

A simple, thread-safe implementation of the **Circuit Breaker** design pattern in Java using the **State Pattern** and a **Sliding Window Log** failure tracking mechanism.

## Purpose

The Circuit Breaker pattern is used to detect failures and encapsulate the logic of preventing a failure from constantly recurring (such as during service maintenance, temporary network issues, or third-party service outages).

---

## State Machine

The circuit breaker transitions between three states: `CLOSED`, `OPEN`, and `HALF_OPEN`.

```mermaid
stateDiagram-v2
    [*] --> CLOSED : Initial State
    
    CLOSED --> OPEN : X failures in last Y seconds
    note on left
        Requests pass through.
        Tracks failures in a sliding window log.
    end note
    
    OPEN --> HALF_OPEN : openDuration elapsed
    note right of OPEN
        Requests are blocked immediately (fast-fail).
        Throws CircuitBreakerOpenException.
    end note

    HALF_OPEN --> CLOSED : onSuccess (probe request succeeds)
    HALF_OPEN --> OPEN : onFailure (probe request fails)
    note right of HALF_OPEN
        Allows exactly one probe request.
        Blocks other parallel requests.
    end note
```

---

## Project Structure

* **`Main.java`**: Entry point of the application, running a simulation of 30 calls to a flaky service with 1-second intervals.
* **`RemoteService.java`**: Simulates a flaky external service with a 70% success rate and a 200ms latency.
* **`config/`**
  * `CircuitBreakerConfig.java`: Configuration holding the `failureThreshold` (X), `slidingWindowDuration` (Y), and `openDuration` (cooldown time before attempting a retry).
* **`exception/`**
  * `CircuitBreakerOpenException.java`: A custom runtime exception thrown when calls are rejected because the circuit is open.
* **`models/`**
  * `State.java`: An enum for the three states (`CLOSED`, `OPEN`, `HALF_OPEN`).
* **`service/`**
  * `CircuitBreaker.java`: The core circuit breaker engine. Implements thread safety using a `ReentrantLock` and wraps calls in the `execute` method.
* **`stateManagement/`**
  * `StateInterface.java`: The interface for state behaviors.
  * `ClosedState.java`: Behavior while the circuit is closed (evaluates sliding window failures).
  * `OpenState.java`: Behavior while the circuit is open.
  * `HalfOpenState.java`: Behavior while the circuit is half-open (handling probe requests).

---

## Key Features

1. **State Pattern**: Isolates state-specific logic into separate class implementations (`ClosedState`, `OpenState`, and `HalfOpenState`) for cleaner code maintenance.
2. **Thread Safety**: Uses a `ReentrantLock` inside the main `CircuitBreaker` class to ensure atomic state transitions and prevent race conditions when multiple threads make calls or modify the failure queue concurrently.
3. **Sliding Window Log**: Uses a `Deque<Instant>` bounded by the `failureThreshold` to implement a memory-efficient sliding window. Old failure timestamps are evicted automatically as new failures arrive, avoiding the need for a background cleanup thread.
4. **Transition Cleanups**: The failure queue is cleared on state transitions (when entering `CLOSED` or `OPEN` states). This prevents stale failure timestamps from lingering and causing the circuit to reopen prematurely on a single subsequent failure.
5. **Single-Probe Constraint**: While in `HALF_OPEN`, only one thread is allowed to perform the probe execution (`probeRunning` check), while concurrent requests continue to throw a `CircuitBreakerOpenException` until the outcome is determined.

---

## How to Run

Compile and run the project from the root directory:

```bash
# Compile the main file (Java automatically compiles dependencies)
javac Main.java

# Run the simulation
java Main
```
