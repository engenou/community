package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Eugen
 * @creat 2022-05-30 17:18
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
