package com.eu.cloud.system.service;

import java.text.ParseException;

public interface ITokenService {


    /**
     * 使令牌token失效
     *
     * @param token
     * @return
     */
    boolean invalidateToken(String token) throws ParseException;


    /**
     * 获取token状态
     * @param token
     * @return 0-有效，1-失效（过期或被加入黑名单）
     */
    int getTokenStatus(String token);
}