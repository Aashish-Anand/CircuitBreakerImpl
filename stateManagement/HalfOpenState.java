package stateManagement;

import java.time.Instant;

import exception.CircuitBreakerOpenException;
import service.CircuitBreaker;
import models.State;

public class HalfOpenState implements StateInterface {

    public State name() {
        return State.HALF_OPEN;
    }
    
    public void beforeExecution(CircuitBreaker cb) {
        if(cb.probeRunning) {
            throw new CircuitBreakerOpenException();
        }
        System.out.println("Sending 1 request to Client. State:HALF_OPEN");
        cb.probeRunning = false;
    }

    public void onSuccess(CircuitBreaker cb) {
        System.out.println("Request Successful. State:Closed");
        cb.consecutiveFailures = 0;
        cb.openedAt = Instant.now();
        cb.stateManagement = new ClosedState();
        cb.probeRunning = false;
    }

    public void onFailure(CircuitBreaker cb) {
        System.out.println("Request Failed. State:OPEN");
        cb.openedAt = Instant.now();
        cb.stateManagement = new OpenState();
        cb.probeRunning = false;
    }
}