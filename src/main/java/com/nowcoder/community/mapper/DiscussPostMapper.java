package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Eugen
 * @creat 2022-05-02 16:21
 */
@Mapper
public interface DiscussPostMapper {

    /**
     * 查询用户帖子列表
     *
     * @param userId 用户 id
     * @param offset 每页起始行行号
     * @param limit 一页显示多少条数据
     * @return
     */
    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit);

    /**
     *  查询帖子的行数
     *  Param()注解：用于给参数取别名。如果只有一个参数，并且在 <if> 里使用，则必须加别名
     *
     * @param userId 用户 id
     * @return 帖子行数
     */
    int selectDiscussPostRows(@Param("userId")int userId);

    /**
     *  发布帖子
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     *  通过 id 查询贴子
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     *  更新帖子数量
     */
    int updateCommentCount(int id, int commentCount);

}
