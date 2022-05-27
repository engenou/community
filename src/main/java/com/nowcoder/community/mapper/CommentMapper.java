package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Eugen
 * @creat 2022-05-06 16:51
 */
@Mapper
public interface CommentMapper {

    /**
     * 根据实体查询评论
     *
     * @param entityType 实体类型 0-帖子(给帖子的评论)  1-评论(给评论的评论：回复)
     * @param entityId   实体 id (评论的实体id：帖子/评论的id)
     * @param offset     每页起始行行号
     * @param limit      一页显示多少条数据
     */
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 根据实体查询评论数
     *
     * @param entityType 实体类型 0-帖子  1-评论
     * @param entityId   实体 id (帖子/评论的id)
     */
    int selectCountByEntity(int entityType, int entityId);

    /**
     * 增加评论
     */
    int insertComment(Comment comment);

    /**
     *  根据id查评论
     */
    Comment selectCommentById(int id);


}
