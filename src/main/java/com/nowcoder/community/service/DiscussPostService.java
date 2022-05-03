package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.mapper.DiscussPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Eugen
 * @creat 2022-05-02 17:02
 */
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPost(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPost(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }


}
