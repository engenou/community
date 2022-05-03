package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Eugen
 * @creat 2022-04-30 16:43
 */
@Service
public class AlphaService {

    public AlphaService() {
        System.out.println("AlphaService构造器。。。");
    }

    @PostConstruct
    public void init(){
        System.out.println("PostConstruct初始化。。。");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("PreDestroy销毁。。。");
    }

    @Autowired
    private AlphaDao alphaDao;

    public String select(){
        return alphaDao.select();
    }
}
