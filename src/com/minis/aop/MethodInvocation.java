package com.minis.aop;

import java.lang.reflect.Method;

//以前通过反射方法调用业务逻辑的那一段代码的包装
public interface MethodInvocation {
    Method getMethod();
    Object[] getArguments();
    Object getThis();
    Object proceed() throws Throwable;
}
