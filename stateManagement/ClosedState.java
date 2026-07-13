package stateManagement;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

import models.State;
import service.CircuitBreaker;

public class ClosedState implements StateInterface {
    private final ReentrantLock lock = new ReentrantLock();
    
    public void beforeExecution(CircuitBreaker cb) {
        return;
        // System.out.println("CircuitBreaker=Closed");
    }

    public void onSuccess(CircuitBreaker cb) {
        return;
        // System.out.println("CircuitBreaker=Closed");
    }

    public void onFailure(CircuitBreaker cb) {
        lock.lock();
        try {
            cb.consecutiveFailures+=1;
            if(cb.consecutiveFailures >= cb.config.failureThreshold) {
                cb.state = State.OPEN;
                cb.openedAt = Instant.now();
                cb.stateManagement = new OpenState();
            }
        } finally {
            lock.unlock();
        }
    }
}