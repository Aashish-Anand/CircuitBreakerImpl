package config;

import java.time.Duration;

public class CircuitBreakerConfig {
    public final int failureThreshold;
    public final Duration openDuration;

    public CircuitBreakerConfig(int failureThreshold, Duration openDuration) {
        this.failureThreshold = failureThreshold;
        this.openDuration = openDuration;
    }
}