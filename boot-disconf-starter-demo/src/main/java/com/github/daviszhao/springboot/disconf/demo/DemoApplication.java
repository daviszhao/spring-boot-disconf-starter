package com.github.daviszhao.springboot.disconf.demo;

import com.github.daviszhao.springboot.disconf.demo.beans.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        context.getEnvironment().getPropertySources().forEach(System.out::println);
        context.getBean(Test.class).output();
    }


}
