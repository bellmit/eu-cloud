package com.eu.cloud.system.service.impl;

import com.eu.cloud.core.constants.AuthConstants;
import com.eu.cloud.core.utils.JWTUtil;
import com.eu.cloud.redis.utils.RedisUtil;
import com.eu.cloud.system.service.ITokenService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author haoxr
 * @date 2021-03-10
 */
@Service
public class TokenServiceImpl implements ITokenService {

//    @Value("${jwt.sign}")
    private final String sign = "!23";

    @Autowired
    private RedisUtil redisUtil;


    @Override
    @SneakyThrows
    public boolean invalidateToken(String token) {

        JWTUtil.JWT jwt = JWTUtil.INSTANCE.check(token, sign);

        // token已过期或则校验失败
        if (jwt.getStatus() != 0) {
            return true;
        }

        // 添加至黑名单使其失效
        redisUtil.set(AuthConstants.TOKEN_BLACKLIST_PREFIX + token, null, (jwt.getExp() - System.currentTimeMillis()) / 1000);
        return true;
    }

    @Override
    public int getTokenStatus(String token) {
        JWTUtil.JWT jwt = JWTUtil.INSTANCE.check(token, sign);
        return jwt.getStatus();
    }

}
