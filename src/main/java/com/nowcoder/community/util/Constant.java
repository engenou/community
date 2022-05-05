package com.nowcoder.community.util;

/**
 * @author Eugen
 * @creat 2022-05-03 18:06
 */
public class Constant {
    /**
     * 激活成功
     */
    public static final int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    public static final int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    public static final int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证的超市时间（12小时）
     */
    public static final int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态下的登录凭证的超时时间（3个月）
     */
    public static final int REMEMBER_EXPIRED_SECONDS = 3600 * 12 * 100;
}
