package com.github.daviszhao.springboot.disconf.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.disconf.retry")
public class RetryProperties {

    /**
     * Initial retry interval in milliseconds.
     */
    private long initialInterval = 1000;
    /**
     * Multiplier for next interval.
     */
    private double multiplier = 1.1;
    /**
     * Maximum interval for backoff.
     */
    private long maxInterval = 2000;
    /**
     * Maximum number of attempts.
     */
    private int maxAttempts = 6;

    public long getInitialInterval() {
        return this.initialInterval;
    }

    public void setInitialInterval(long initialInterval) {
        this.initialInterval = initialInterval;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public long getMaxInterval() {
        return this.maxInterval;
    }

    public void setMaxInterval(long maxInterval) {
        this.maxInterval = maxInterval;
    }

    public int getMaxAttempts() {
        return this.maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

}

