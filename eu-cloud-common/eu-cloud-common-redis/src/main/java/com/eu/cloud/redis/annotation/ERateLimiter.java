package com.eu.cloud.redis.annotation;

import java.lang.annotation.*;

/**
 * 限流的注解
 *
 * @author jiangxd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ERateLimiter {

    /**
     * 超时时间 (s)
     *
     * @return
     */
    int lockTime();

    /**
     * redis 限流key的
     *
     * @return redis 限流key
     */
    String limitKey() default "";

}
