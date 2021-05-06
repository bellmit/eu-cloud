package com.eu.cloud.server.system.api.client;

import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author haoxr
 * @createTime 2021/4/24 21:30
 */
@Component
@Slf4j
public class UserFeignFallbackClient implements UserFeignClient {

    @Override
    public GlobalResponseWrapper getUserByUsername(String username) {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return new GlobalResponseWrapper(GlobalExceptionCode.FEIGN_ERROR);
    }
}
