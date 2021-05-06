package com.eu.cloud.redis.annotation;

import java.lang.annotation.*;

/**
 * 防止重复提交的注解
 *
 * @author jiangxd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ERepeat {

    /**
     * 超时时间 (s)
     *
     * @return
     */
    int lockTime();

    /**
     * redis 锁key的
     *
     * @return redis 锁key
     */
    String lockKey() default "";

}
