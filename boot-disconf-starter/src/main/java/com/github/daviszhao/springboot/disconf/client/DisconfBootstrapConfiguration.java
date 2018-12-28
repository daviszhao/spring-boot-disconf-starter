package com.github.daviszhao.springboot.disconf.client;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
@EnableConfigurationProperties
public class DisconfBootstrapConfiguration {

    private final ConfigurableEnvironment environment;

    @Autowired
    public DisconfBootstrapConfiguration(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Bean
    public DisconfProperties configClientProperties() {
        return new DisconfProperties(this.environment);
    }

    @Bean
    @ConditionalOnMissingBean(DisconfServicePropertySourceLocator.class)
    @ConditionalOnProperty(value = "spring.disconf.enabled", matchIfMissing = true)
    public DisconfServicePropertySourceLocator configServicePropertySource(DisconfProperties properties) {
        return new DisconfServicePropertySourceLocator(properties);
    }

    @ConditionalOnProperty(value = "spring.disconf.failFast")
    @ConditionalOnClass({Retryable.class, Aspect.class, AopAutoConfiguration.class})
    @Configuration
    @EnableRetry(proxyTargetClass = true)
    @Import(AopAutoConfiguration.class)
    @EnableConfigurationProperties(RetryProperties.class)
    protected static class RetryConfiguration {

        @Bean
        @ConditionalOnMissingBean(name = "configServerRetryInterceptor")
        public RetryOperationsInterceptor configServerRetryInterceptor(
                RetryProperties properties) {
            return RetryInterceptorBuilder
                    .stateless()
                    .backOffOptions(properties.getInitialInterval(),
                            properties.getMultiplier(), properties.getMaxInterval())
                    .maxAttempts(properties.getMaxAttempts()).build();
        }
    }

}
