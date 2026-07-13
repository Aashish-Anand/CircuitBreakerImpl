package stateManagement;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

import service.CircuitBreaker;
import models.State;

public class HalfOpenState implements StateInterface {
    private final ReentrantLock lock = new ReentrantLock();
    
    public void beforeExecution(CircuitBreaker cb) {
        lock.lock();
        try {
            System.out.println("Sending 1 request to Client. State:HALF_OPEN");
        } finally {
            lock.unlock();
        }
        
        return;
    }

    public void onSuccess(CircuitBreaker cb) {
        lock.lock();
        try {
            System.out.println("Request Successful. State:Closed");
            cb.state = State.CLOSED;
            cb.consecutiveFailures = 0;
            cb.openedAt = Instant.now();
            cb.stateManagement = new ClosedState();
        } finally {
            lock.unlock();
        }
    }

    public void onFailure(CircuitBreaker cb) {
        lock.lock();
        try {
            System.out.println("Request Successful. State:Closed");
            cb.state = State.OPEN;
            cb.openedAt = Instant.now();
            cb.stateManagement = new OpenState();
        } finally {
            lock.unlock();
        }
    }
}