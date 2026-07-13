package service;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

import config.CircuitBreakerConfig;
import models.State;
import stateManagement.ClosedState;
import stateManagement.StateInterface;
import java.util.function.Supplier;

public class CircuitBreaker {
    public final CircuitBreakerConfig config;
    public StateInterface stateManagement;
    // private final ReentrantLock lock;
    public State state;
    public int consecutiveFailures = 0;
    public Instant openedAt;
    public boolean probeRunning = false;

    public CircuitBreaker(CircuitBreakerConfig config) {
        this.config = config;
        stateManagement = new ClosedState();
        // this.lock = new ReentrantLock();
        this.state = State.CLOSED;
    }

    public <T> T execute(Supplier<T> supplier) {
        beforeExecution();
        try {
            T result = supplier.get();
            onSuccess();
            return result;
        } catch (Exception ex) {
            onFailure();
            throw ex;
        }
    }

    public void beforeExecution() {
        stateManagement.beforeExecution(this);
    }

    public void onSuccess() {
        stateManagement.onSuccess(this);
    }

    public void onFailure() {
        stateManagement.onFailure(this);
    }
}