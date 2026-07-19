package stateManagement;

import java.time.Duration;
import java.time.Instant;

import exception.CircuitBreakerOpenException;
import models.State;
import service.CircuitBreaker;

public class OpenState implements StateInterface {

    public State name() {
        return State.OPEN;
    }

    public void beforeExecution(CircuitBreaker cb) {
        Duration elapsed = Duration.between(cb.openedAt, Instant.now());
        if (elapsed.compareTo(cb.config.openDuration) >= 0) {
            cb.probeRunning = true;
            cb.stateManagement = cb.HALF_OPEN_STATE;
            System.out.println("Circuit Breaker is set to Half_Open, probing in progress.");
            return;
        }
        throw new CircuitBreakerOpenException();
    }

    public void onSuccess(CircuitBreaker cb) {
        cb.probeRunning = false;
    }

    public void onFailure(CircuitBreaker cb) {
        // System.out.println("Circuit Breaker is already open");
    }
}