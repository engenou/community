package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author Eugen
 * @creat 2022-05-07 10:36
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);// 默认0；未读
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(Constant.TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        // 实体用户Id，帖子的或评论的，去相应的数据库表查userId
        if(comment.getEntityType() == Constant.ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType() == Constant.ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        // 发送消息
        eventProducer.fireEvent(event);

        // 评论的是帖子时，评论数改变了。重新发布帖子覆盖原来的es数据
        if(comment.getEntityType() == Constant.ENTITY_TYPE_POST){
            // 触发发帖事件
            event = new Event()
                    .setTopic(Constant.TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(Constant.ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);

            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
