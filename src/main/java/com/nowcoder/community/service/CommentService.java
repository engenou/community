package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.mapper.CommentMapper;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Eugen
 * @creat 2022-05-06 17:01
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    // 根据实体类型查询评论
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentByEntity(entityType,entityId,offset,limit);
    }

    // 查询评论数
    public int findCommentCountByEntity(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    // 添加评论
    public int addComment(Comment comment){
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);
        // 跟新帖子数量
        if(comment.getEntityType() == Constant.ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    // 根据id查评论
    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }

}
