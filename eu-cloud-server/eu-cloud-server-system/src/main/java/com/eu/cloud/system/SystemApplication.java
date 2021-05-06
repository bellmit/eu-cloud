package com.eu.cloud.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 系统服务
 *
 * @author jiangxd
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.eu")
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class);
        log.info("============================ System * Service startup completed ===================================================");
    }
}