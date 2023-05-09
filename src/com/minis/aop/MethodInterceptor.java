package com.minis.aop;

/**
 * MethodInterceptor 就是方法上的拦截器，对外就是一个 invoke() 方法。
 * 拦截器不仅仅会增强逻辑，它内部也会调用业务逻辑方法。
 * 因此，对外部程序而言，只需要使用这个 MethodInterceptor 就可以了。
 */
public interface MethodInterceptor extends Interceptor{
    Object invoke(MethodInvocation invocation) throws Throwable;
}