package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 *  登录凭证（后面会改成 Redis保存）
 * @author Eugen
 * @creat 2022-05-04 10:29
 */
@Data
public class LoginTicket {

    private Integer id;

    private Integer userId;

    private String ticket;

    //0:有效  1:无效
    private Integer status;

    // 过期时间
    private Date expired;
}
