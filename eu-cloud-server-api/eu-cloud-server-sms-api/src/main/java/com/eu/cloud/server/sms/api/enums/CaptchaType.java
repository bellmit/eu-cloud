package com.eu.cloud.server.sms.api.enums;

/**
 * 验证码类型
 *
 * @author jiangxd
 */
public enum CaptchaType {

    LOGIN(1, "登录"),
    REGISTER(2, "注册"),
    RESET(3, "重置"),

    ;

    /**
     * 代码
     */
    private final int code;

    /**
     * 内容
     */
    private final String msg;


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    CaptchaType(int code, String msg) {
        this.code = code;
        this.msg = msg;

    }
}
