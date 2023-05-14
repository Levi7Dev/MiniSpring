package com.minis.aop;

/**
 * Pointcut 就是一个定义在何处插入切面逻辑的规则。
 */
public interface Pointcut {
    MethodMatcher getMethodMatcher();
}
