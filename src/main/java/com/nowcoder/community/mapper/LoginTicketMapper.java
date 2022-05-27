package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 *  ticket后续存入redis，不用mysql
 * @author Eugen
 * @creat 2022-05-04 10:30
 */
@Mapper
@Deprecated//登录凭证存入redis
public interface LoginTicketMapper {

    /**
     *  保存ticket
     */
    @Insert({
           "insert into login_ticket(user_id,ticket,status,expired) ",
           "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     *  查询ticket
     */
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     *  更新ticket状态
     */
    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket, int status);

    //注解中语句也可以拼 <if></if>等语句，前后加上<script></script>

}
