package com.eu.cloud.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * File 启动类
 *
 * @author jiangxd
 */
@Slf4j
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.eu")
@SpringBootApplication(scanBasePackages = "com.eu")
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
        log.info("============================ FILE * Service startup completed ===================================================");
    }

}
