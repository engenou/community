package com.nowcoder.community.aspect;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author Eugen
 * @creat 2022-05-08 19:42
 */
//@Component
//@Aspect //报错error
public class AlphaAspect {


    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){

    }

    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }
    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }
    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }
    @Around("pointcut()")
    public void around(){
        System.out.println("around");
    }


}
