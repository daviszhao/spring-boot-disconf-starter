package com.github.daviszhao.springboot.disconf.client;


import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DisconfClientAutoConfiguration {

    @Bean
    public DisconfProperties configClientProperties(Environment environment,
                                                    ApplicationContext context) {
        if (context.getParent() != null
                && BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                context.getParent(), DisconfProperties.class).length > 0) {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(context.getParent(),
                    DisconfProperties.class);
        }
        return new DisconfProperties(environment);
    }

    @Bean
    public DisconfHealthProperties disconfClientHealthProperties() {
        return new DisconfHealthProperties();
    }

    @Configuration
    @ConditionalOnClass(HealthIndicator.class)
    @ConditionalOnBean(DisconfServicePropertySourceLocator.class)
    @ConditionalOnProperty(value = "health.config.enabled", matchIfMissing = true)
    protected static class ConfigServerHealthIndicatorConfiguration {

        @Bean
        public DisconfServerHealthIndicator configServerHealthIndicator(
                DisconfServicePropertySourceLocator locator,
                DisconfHealthProperties properties, Environment environment) {
            return new DisconfServerHealthIndicator(locator, environment, properties);
        }
    }

    @Configuration
    @ConditionalOnClass(ContextRefresher.class)
    @ConditionalOnBean(ContextRefresher.class)
    @ConditionalOnProperty(value = "spring.disconf.watch.enabled")
    protected static class DisconfClientWatchConfiguration {

        @Bean
        public DisconfClientWatch disconfClientWatch(ContextRefresher contextRefresher) {
            return new DisconfClientWatch(contextRefresher);
        }
    }

}