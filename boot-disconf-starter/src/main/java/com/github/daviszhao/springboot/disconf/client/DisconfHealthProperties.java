package com.github.daviszhao.springboot.disconf.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@SuppressWarnings("ConfigurationProperties")
@ConfigurationProperties("health.config")
public class DisconfHealthProperties {
    /**
     * Flag to indicate that the config server health indicator should be installed.
     */
    private boolean enabled;

    /**
     * Time to live for cached result, in milliseconds. Default 300000 (5 min).
     */
    private long timeToLive = 60 * 5 * 1000;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }
}
