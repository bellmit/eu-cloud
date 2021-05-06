package com.eu.cloud.server.sms.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * SMS 服务 Feign 客户端
 *
 * @author jiangxd
 */
@Primary
@FeignClient(value = "eu-sms", fallbackFactory = SmsFallback.class)
public interface SmsClient {

    /**
     * 检查短信验证码是否合法
     *
     * @param phoneNumber
     * @param captcha
     * @param captchaType
     * @return
     */
    @GetMapping("/captcha/check")
    boolean captchaCheck(@RequestParam("phoneNumber") String phoneNumber,
                         @RequestParam("captcha") String captcha,
                         @RequestParam("captchaType") Integer captchaType);

}
