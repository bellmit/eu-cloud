package com.eu.cloud.server.system.api.client;

import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "eu-system", fallback = UserFeignFallbackClient.class)
public interface UserFeignClient {

    @GetMapping("/users/username/{username}")
    GlobalResponseWrapper getUserByUsername(@PathVariable String username);
}
