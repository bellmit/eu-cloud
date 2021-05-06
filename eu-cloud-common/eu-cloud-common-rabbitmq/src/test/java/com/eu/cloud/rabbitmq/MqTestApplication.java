package com.eu.cloud.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.eu")
@EnableAspectJAutoProxy
public class MqTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqTestApplication.class, args);
    }

}