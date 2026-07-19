package service;

import java.time.Instant;

import config.CircuitBreakerConfig;
import stateManagement.ClosedState;
import stateManagement.HalfOpenState;
import stateManagement.OpenState;
import stateManagement.StateInterface;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class CircuitBreaker {
    public final CircuitBreakerConfig config;
    public StateInterface stateManagement;
    private final ReentrantLock lock;
    public Instant openedAt;
    public boolean probeRunning = false;
    public Deque<Instant> failureTimestamps; // we will always maintain the size of this queue = failureThreshold

    public final StateInterface CLOSED_STATE = new ClosedState();
    public final StateInterface OPEN_STATE = new OpenState();
    public final StateInterface HALF_OPEN_STATE = new HalfOpenState();

    public CircuitBreaker(CircuitBreakerConfig config) {
        this.config = config;
        stateManagement = CLOSED_STATE;
        this.lock = new ReentrantLock();
        failureTimestamps = new ArrayDeque<>();
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

    public void updateFailureQueue() {
        failureTimestamps.addLast(Instant.now());
        if (failureTimestamps.size() > config.failureThreshold) {
            failureTimestamps.removeFirst();
        }
    }
}
