package com.eu.cloud.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 监控服务
 *
 * @author jiangxd
 */
@Slf4j
@EnableAdminServer
@SpringBootApplication
public class MonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class);
        log.info("============================ Monitor * Service startup completed ===================================================");
    }
}