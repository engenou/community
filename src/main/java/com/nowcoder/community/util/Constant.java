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


    /**
     * 实体类型：帖子
     */
    public static final int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    public static final int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    public static final int ENTITY_TYPE_USER = 3;


    /**
     *  主题
     */
    // 评论
    public static final String TOPIC_COMMENT = "comment";
    // 点赞
    public static final String TOPIC_LIKE = "like";
    // 关注
    public static final String TOPIC_FOLLOW = "folloW";

    /**
     *  系统用户 Id
     */
    public static final int SYSTEM_USER_ID = 1;

    /**
     *  权限
     */
    // 1.普通用户
    public static final String AUTHORITY_USER = "user";
    // 2.管理员
    public static final String AUTHORITY_ADMIN = "admin";
    // 3.版主
    public static final String AUTHORITY_MODERATOR = "moderator";

}
