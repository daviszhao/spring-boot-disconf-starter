package com.github.daviszhao.springboot.disconf.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.util.StringUtils.hasText;

@Slf4j(topic = "DisconfServicePropertySourceLocator")
public class DisconfClientWatch implements Closeable, EnvironmentAware {


    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ContextRefresher refresher;
    private Environment environment;

    public DisconfClientWatch(ContextRefresher refresher) {
        this.refresher = refresher;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void start() {
        this.running.compareAndSet(false, true);
    }

    @Scheduled(initialDelayString = "${spring.disconf.watch.initialDelay:180000}", fixedDelayString = "${spring.disconf.watch.delay:500}")
    public void watchConfigServer() {
        if (this.running.get()) {
            String newState = this.environment.getProperty("config.client.state");
            String oldState = DisconfStateHolder.getState();

            // only refresh if state has changed
            if (stateChanged(oldState, newState)) {
                DisconfStateHolder.setState(newState);
                this.refresher.refresh();
            }
        }
    }

    /* for testing */
    private boolean stateChanged(String oldState, String newState) {
        return (!hasText(oldState) && hasText(newState))
                || (hasText(oldState) && !oldState.equals(newState));
    }

    @Override
    public void close() {
        this.running.compareAndSet(true, false);
    }

}

