package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

    // 解决netty启动冲突：es 和 redis
    @PostConstruct
    public void init(){
        // 解决netty冲突, Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }


    public static void main(String[] args) {

        SpringApplication.run(CommunityApplication.class, args);
    }

}
