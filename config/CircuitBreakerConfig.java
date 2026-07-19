package config;

import java.time.Duration;

public class CircuitBreakerConfig {
    public final int failureThreshold;
    public final Duration slidingWindowDuration;
    public final Duration openDuration;

    public CircuitBreakerConfig(int failureThreshold, Duration slidingWindowDuration, Duration openDuration) {
        this.failureThreshold = failureThreshold;
        this.slidingWindowDuration = slidingWindowDuration;
        this.openDuration = openDuration;
    }
}