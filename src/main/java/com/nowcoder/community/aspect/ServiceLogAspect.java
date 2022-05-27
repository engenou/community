package com.nowcoder.community.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author Eugen
 * @creat 2022-05-08 20:32
 */
//@Aspect
//@Component
public class ServiceLogAspect {

    // 提取公共切入点
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){

    }

    @Before("pointcut()")
    public void before(){

    }

}
