package com.eu.cloud.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 授权中心服务
 *
 * @author jiangxd
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.eu")
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class);
        log.info("============================ Auth * Service startup completed ===================================================");
    }
}