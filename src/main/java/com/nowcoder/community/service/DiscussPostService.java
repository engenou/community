package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.mapper.DiscussPostMapper;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Eugen
 * @creat 2022-05-02 17:02
 */
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    // 查询帖子
    public List<DiscussPost> findDiscussPost(int userId, int offset, int limit, int orderMode){
        return discussPostMapper.selectDiscussPost(userId, offset, limit, orderMode);
    }

    // 查询帖子总数
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    // 发布帖子
    public int addDisCussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        // 转义html标签
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    // 根据id查询帖子
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    // 更新帖子的评论数量
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    // 更新帖子类型：置顶
    public int updateType(int id, int type){
        return discussPostMapper.updateType(id, type);
    }

    // 更新帖子状态：普通  加精
    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }

    // 更新帖子分数
    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }

}
