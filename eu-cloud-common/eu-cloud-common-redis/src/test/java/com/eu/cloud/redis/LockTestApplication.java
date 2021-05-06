package com.eu.cloud.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.eu")
@EnableAspectJAutoProxy
public class LockTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LockTestApplication.class, args);
    }

}