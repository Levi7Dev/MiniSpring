package com.minis.test.service;

import com.minis.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class MyBeforeAdvice implements MethodBeforeAdvice {

    //具体增强的方法
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("----------my interceptor before method call----------");
    }
}
