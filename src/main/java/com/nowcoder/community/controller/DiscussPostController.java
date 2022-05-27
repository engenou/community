package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Eugen
 * @creat 2022-05-06 11:49
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJsonString(403,"您还没有登录！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDisCussPost(post);

        // 出现异常，将来统一处理
        return CommunityUtil.getJsonString(0,"发布成功");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDisCussPost(@PathVariable("discussPostId") int discussPostId,
                                 Model model, Page page){
        // 查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        // 查找作者
        User user = userService.findUserBuId(post.getUserId());
        model.addAttribute("user",user);

        // 点赞数量，状态
        long likeCount = likeService.findEntityLikeCount(Constant.ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount",likeCount);
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), Constant.ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus",likeStatus);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // ===========评论：给帖子的评论============
        // ===========回复：给评论的评论============
        //评论列表
        List<Comment> commentList = commentService.findCommentByEntity(Constant.ENTITY_TYPE_POST,
                post.getId(),page.getOffset(),page.getLimit());
        // 评论Vo列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment : commentList){
                // 评论Vo
                Map<String,Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserBuId(comment.getUserId()));

                // 评论点赞数量，状态
                likeCount = likeService.findEntityLikeCount(Constant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount",likeCount);
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), Constant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus",likeStatus);

                // 回复列表
                List<Comment> replyList = commentService.findCommentByEntity(
                        Constant.ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                // 回复Vo列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        // 回复Vo
                        Map<String,Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserBuId(reply.getUserId()));

                        // 回复点赞数量，状态
                        likeCount = likeService.findEntityLikeCount(Constant.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount",likeCount);
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), Constant.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus",likeStatus);

                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserBuId(reply.getTargetId());
                        replyVo.put("target",target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);

                // 回复数量
                int replyConut = commentService.findCommentCountByEntity(Constant.ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyConut);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments",commentVoList);
        return "site/discuss-detail";
    }

}
