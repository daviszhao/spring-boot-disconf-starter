package com.github.daviszhao.springboot.disconf.demo.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Test {
    @Value("${a:default}")
    private String a;

    public void output() {
        System.out.println(a);
    }
}
