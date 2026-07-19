package service;
import java.time.Instant;

import config.CircuitBreakerConfig;
import stateManagement.ClosedState;
import stateManagement.StateInterface;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class CircuitBreaker {
    public final CircuitBreakerConfig config;
    public StateInterface stateManagement;
    private final ReentrantLock lock;
    public int consecutiveFailures = 0;
    public Instant openedAt;
    public boolean probeRunning = false;

    public CircuitBreaker(CircuitBreakerConfig config) {
        this.config = config;
        stateManagement = new ClosedState();
        this.lock = new ReentrantLock();
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
        lock.lock();
        try {
            stateManagement.beforeExecution(this);
        } finally {
            lock.unlock();
        }
    }

    public void onSuccess() {
        lock.lock(); 
        try {
            stateManagement.onSuccess(this);
        } finally {
            lock.unlock();
        }
    }

    public void onFailure() {
        lock.lock(); 
        try {
            stateManagement.onFailure(this);
        } finally {
            lock.unlock();
        }
    }
}