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
        cb.consecutiveFailures+=1;
        if(cb.consecutiveFailures >= cb.config.failureThreshold) {
            cb.openedAt = Instant.now();
            cb.stateManagement = new OpenState();
        }
    }
}