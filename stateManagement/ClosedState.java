package stateManagement;

import java.time.Instant;

import models.State;
import service.CircuitBreaker;

public class ClosedState implements StateInterface {

    public State name() {
        return State.CLOSED;
    }

    public void beforeExecution(CircuitBreaker cb) {
        return;
    }

    public void onSuccess(CircuitBreaker cb) {
        return;
    }

    public void onFailure(CircuitBreaker cb) {
        cb.updateFailureQueue();
        Instant windowStart = Instant.now().minus(cb.config.slidingWindowDuration);
        if (cb.failureTimestamps.size() >= cb.config.failureThreshold
                && cb.failureTimestamps.getFirst().isAfter(windowStart)) {
            cb.openedAt = Instant.now();
            cb.stateManagement = cb.OPEN_STATE;
            cb.failureTimestamps.clear();
        }
    }
}