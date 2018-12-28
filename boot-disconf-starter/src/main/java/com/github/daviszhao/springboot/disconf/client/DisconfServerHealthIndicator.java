package com.github.daviszhao.springboot.disconf.client;


import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.ArrayList;
import java.util.List;

public class DisconfServerHealthIndicator extends AbstractHealthIndicator {

    private final DisconfServicePropertySourceLocator locator;
    private final DisconfHealthProperties properties;
    private final Environment environment;

    private long lastAccess = 0;

    private PropertySource<?> cached;

    public DisconfServerHealthIndicator(DisconfServicePropertySourceLocator locator,
                                        Environment environment, DisconfHealthProperties properties) {
        this.environment = environment;
        this.locator = locator;
        this.properties = properties;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        PropertySource<?> propertySource = getPropertySource();
        builder.up();
        if (propertySource instanceof CompositePropertySource) {
            List<String> sources = new ArrayList<>();
            for (PropertySource<?> ps : ((CompositePropertySource) propertySource).getPropertySources()) {
                sources.add(ps.getName());
            }
            builder.withDetail("propertySources", sources);
        } else if (propertySource != null) {
            builder.withDetail("propertySources", propertySource.toString());
        } else {
            builder.unknown().withDetail("error", "no property sources located");
        }
    }

    private PropertySource<?> getPropertySource() {
        long accessTime = System.currentTimeMillis();
        if (isCacheStale(accessTime)) {
            this.lastAccess = accessTime;
            this.cached = locator.locate(this.environment);
        }
        return this.cached;
    }

    private boolean isCacheStale(long accessTime) {
        if (this.cached == null) {
            return true;
        }
        return (accessTime - this.lastAccess) >= this.properties.getTimeToLive();
    }

}
