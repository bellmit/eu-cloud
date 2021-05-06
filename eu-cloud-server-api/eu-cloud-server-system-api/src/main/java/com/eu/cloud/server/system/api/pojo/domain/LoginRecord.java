package com.eu.cloud.server.system.api.pojo.domain;

import lombok.Data;

/**
 * @author hxr
 * @date 2021-03-09
 */
@Data
public class LoginRecord {

    private String clientIP;

    private long elapsedTime;

    private Object message;

    private String token;

    private String username;

    private String loginTime;

    private String region;

    /**
     * 会话状态 0-离线 1-在线
     */
    private Integer status;

}
