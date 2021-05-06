package com.eu.cloud.redis.aspect;

import com.eu.cloud.redis.annotation.ERateLimiter;
import com.eu.cloud.redis.annotation.ERepeat;
import com.eu.cloud.redis.client.RedissonLockClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 分布式限流器拦截器
 *
 * @author jiangxd
 */
@Aspect
@Component
public class RateLimiterAspect extends BaseAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private RedissonLockClient redissonLockClient;

    /***
     * 定义controller切入点拦截规则，拦截ERateLimiter注解的业务方法
     */
    @Pointcut("@annotation(eRateLimiter)")
    public void pointCut(ERateLimiter eRateLimiter) {
    }


    /**
     * AOP分布式限流拦截
     *
     * @param joinPoint
     * @return
     * @throws Exception
     */
    @Around("pointCut(eRateLimiter)")
    public Object repeatSubmit(ProceedingJoinPoint joinPoint, ERateLimiter eRateLimiter) throws Throwable {
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        if (Objects.nonNull(eRateLimiter)) {
            // 获取参数
            Object[] args = joinPoint.getArgs();
            // 进行一些参数的处理，比如获取订单号，操作人id等
            String key = getValueBySpEL(eRateLimiter.limitKey(), parameterNames, args, "rateLimiter").get(0);
            // 公平加锁，lockTime后锁自动释放
            RRateLimiter rRateLimiter;
            try {
                rRateLimiter = redissonClient.getRateLimiter(key);
                // 如果成功获取到锁就继续执行
                if (rRateLimiter.tryAcquire()) {
                    // 执行进程
                    return joinPoint.proceed();
                } else {
                    // 未获取到锁
                    throw new Exception("操作过于频繁，请稍后重试");
                }
            } finally {
                // 如果锁还存在，在方法执行完成后，释放锁
//                if (isLocked) {
                    redissonLockClient.unlock(key);
//                }
            }
        }

        return joinPoint.proceed();
    }

}
