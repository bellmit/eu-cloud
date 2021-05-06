package com.eu.cloud.sms.enums;

import com.eu.cloud.core.exception.GlobalException;
import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证码类型
 *
 * @author jiangxd
 */
@Slf4j
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
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

    /**
     * 根据代码获取枚举
     * 不要尝试缓存全部的枚举，该方法用到的频率不会太高，且枚举很少，不会造成资源浪费
     * <p>
     * 使用 @JsonCreator 让 jackson 解析 json 的时候能匹配到该枚举
     * 参考：https://segmentfault.com/q/1010000020636087
     *
     * @param code
     * @return
     */
    @JsonCreator
    public static CaptchaType getByCode(int code) {

        CaptchaType[] values = CaptchaType.values();

        for (CaptchaType value : values) {
            if (value.getCode() == code) {
                return value;
            }
        }

        throw new GlobalException(GlobalExceptionCode.NOT_FOUND_ENUM, String.format("未找到 [%s] 对应的短信验证码类型!", code));
    }
}
