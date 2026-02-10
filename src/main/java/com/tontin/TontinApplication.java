package com.tontin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.tontin")
public class TontinApplication {
    public static void main(String[] args) {
        SpringApplication.run(TontinApplication.class, args);
    }
}
