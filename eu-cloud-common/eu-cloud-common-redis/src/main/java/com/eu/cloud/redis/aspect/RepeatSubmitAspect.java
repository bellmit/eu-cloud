package com.eu.cloud.redis.aspect;

import com.eu.cloud.redis.annotation.ERepeat;
import com.eu.cloud.redis.client.RedissonLockClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复提交分布式锁拦截器
 *
 * @author 2019年6月18日
 */
@Aspect
@Component
public class RepeatSubmitAspect extends BaseAspect {

    @Resource
    private RedissonLockClient redissonLockClient;

    /***
     * 定义controller切入点拦截规则，拦截ERepeat注解的业务方法
     */
    @Pointcut("@annotation(eRepeat)")
    public void pointCut(ERepeat eRepeat) {
    }


    /**
     * AOP分布式锁拦截
     *
     * @param joinPoint
     * @return
     * @throws Exception
     */
    @Around("pointCut(eRepeat)")
    public Object repeatSubmit(ProceedingJoinPoint joinPoint, ERepeat eRepeat) throws Throwable {
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        if (Objects.nonNull(eRepeat)) {
            // 获取参数
            Object[] args = joinPoint.getArgs();
            // 进行一些参数的处理，比如获取订单号，操作人id等
            String key = getValueBySpEL(eRepeat.lockKey(), parameterNames, args, "RepeatSubmit").get(0);
            // 公平加锁，lockTime后锁自动释放
            boolean isLocked = false;
            try {
                isLocked = redissonLockClient.fairLock(key, TimeUnit.SECONDS, eRepeat.lockTime());
                // 如果成功获取到锁就继续执行
                if (isLocked) {
                    // 执行进程
                    return joinPoint.proceed();
                } else {
                    // 未获取到锁
                    throw new Exception("操作过于频繁，请稍后重试");
                }
            } finally {
                // 如果锁还存在，在方法执行完成后，释放锁
                if (isLocked) {
                    redissonLockClient.unlock(key);
                }
            }
        }

        return joinPoint.proceed();
    }

}
