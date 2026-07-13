package stateManagement;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

import exception.CircuitBreakerOpenException;
import models.State;
import service.CircuitBreaker;

public class OpenState implements StateInterface {

    private final ReentrantLock lock = new ReentrantLock();
    
    public void beforeExecution(CircuitBreaker cb) {
        lock.lock();
        try {
            Duration elapsed = Duration.between(cb.openedAt, Instant.now());
            if(elapsed.compareTo(cb.config.openDuration) >= 0) {
                cb.state = State.HALF_OPEN;
                cb.probeRunning = true;
                cb.stateManagement = new HalfOpenState();
                return;
            }
            throw new CircuitBreakerOpenException();
        } finally {
            lock.unlock();
        }
    }

    public void onSuccess(CircuitBreaker cb) {
        lock.lock();
        try {
            cb.consecutiveFailures = 0;
            cb.state = State.CLOSED;
            cb.probeRunning = false;
        } finally {
            lock.unlock();
        }
    }

    public void onFailure(CircuitBreaker cb) {
        // System.out.println("Circuit Breaker is already open");
    }
}