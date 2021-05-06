package com.eu.cloud.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * SMS 启动类
 *
 * @author jiangxd
 */
@Slf4j
@EnableFeignClients(basePackages = "com.eu")
@SpringBootApplication(scanBasePackages = "com.eu")
public class SmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
        log.info("============================ SMS * Service startup completed ===================================================");
    }

}
