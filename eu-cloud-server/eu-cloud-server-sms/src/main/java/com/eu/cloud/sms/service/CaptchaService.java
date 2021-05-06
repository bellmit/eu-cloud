package com.eu.cloud.sms.service;

import cn.hutool.core.util.RandomUtil;
import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import com.eu.cloud.redis.client.JedisClient;
import com.eu.cloud.redis.client.RedissonLockClient;
import com.eu.cloud.redis.constants.RedisKey;
import com.eu.cloud.sms.enums.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 短信验证码 service
 *
 * @author jiangxd
 */
@Slf4j
@Service
public class CaptchaService {

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private AliyunService aliyunService;

    @Autowired
    private RedissonLockClient redissonLockClient;

    /**
     * 短信发送时间间隔
     */
    @Value("${message.send.interval}")
    private Integer messageSendInterval;

    /**
     * 短信验证码有效期
     */
    @Value("${captcha.expiration}")
    private Integer captchaExpiration;


    /**
     * 短信验证码发送
     *
     * @param phoneNumber
     * @param messageType
     * @return
     */
    public GlobalResponseWrapper send(String phoneNumber, MessageType messageType) {

        RLock lock = redissonLockClient.getLock(String.format(RedisKey.LOCK_SMS_CAPTCHA, messageType.getCode(), phoneNumber));

        if (!lock.tryLock()) {
            log.error("获取分布式锁失败,向手机号[{}]发送短信验证码[{}]失败", phoneNumber, messageType.getMsg());
            lock.unlock();
            return new GlobalResponseWrapper(GlobalExceptionCode.ERROR).msg("获取分布式锁失败");
        }

        //检查短信验证码是否存在 , 若存在则反馈短信验证码发送频繁
        boolean captchaSendMarkExists = this.jedisClient.operate(jedis -> jedis.exists(String.format(RedisKey.KEY_SMS_CAPTCHA_SEND_MARK, messageType.getCode(), phoneNumber)));
        if (captchaSendMarkExists) {
            lock.unlock();
            return new GlobalResponseWrapper(GlobalExceptionCode.MESSAGE_SEND_FREQUENTLY);
        }

        //得到一个长度为 6 的随机数字符串
        int captcha = RandomUtil.randomInt(100000, 999999);

        //若短信发送失败则直接反馈
        boolean isSend = this.aliyunService.sendMessage(phoneNumber, messageType, String.format(messageType.getTemplateParams(), captcha));
        if (!isSend) {
            lock.unlock();
            return new GlobalResponseWrapper(GlobalExceptionCode.MESSAGE_SEND_ERROR);
        }

        this.jedisClient.operate(jedis -> {
            jedis.setex(String.format(RedisKey.KEY_SMS_CAPTCHA_SEND_MARK, messageType.getCode(), phoneNumber), CaptchaService.this.messageSendInterval, String.valueOf(captcha));
            jedis.setex(String.format(RedisKey.KEY_SMS_CAPTCHA, messageType.getCode(), phoneNumber), CaptchaService.this.captchaExpiration, String.valueOf(captcha));
            return null;    //此函数要求必须存在返回值 , 此处返回一个 null
        });

        lock.unlock();
        return new GlobalResponseWrapper();

    }


    /**
     * 检查短信验证码是否正确
     *
     * @param phoneNumber
     * @param captcha
     * @param messageType
     * @return
     */
    public boolean check(String phoneNumber, String captcha, MessageType messageType) {

        String oldCaptcha = this.jedisClient.operate(jedis -> jedis.get(String.format(RedisKey.KEY_SMS_CAPTCHA, messageType.getCode(), phoneNumber)));

        if (captcha.equals(oldCaptcha)) {
            this.jedisClient.operate(jedis -> jedis.del(String.format(RedisKey.KEY_SMS_CAPTCHA, messageType.getCode(), phoneNumber)));
            return Boolean.TRUE;
        } else
            return Boolean.FALSE;
    }

}
