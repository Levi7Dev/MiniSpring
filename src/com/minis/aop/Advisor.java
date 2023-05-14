package com.minis.aop;

/**
 * Advisor 是一个重要的接口，它用于将一个 Advice（切面逻辑）与一个 Pointcut（切入点）关联起来。
 * Advice 是我们编写的横切关注点的代码，例如日志、事务管理等，
 * 而 Pointcut 则定义了在何处应用这些横切关注点。
 * 因此，Advisor 负责将增强的行为应用于特定的切点。
 */
public interface Advisor {
    MethodInterceptor getMethodInterceptor();
    void setMethodInterceptor(MethodInterceptor methodInterceptor);
    Advice getAdvice();
}