package com.minis.aop;

import java.lang.reflect.Method;

/**
 * 1.用来封装调用的方法、目标对象以及方法参数；
 * 2.负责执行目标方法,proceed方法实现，当所有的增强都被执行后，它会调用目标方法并返回结果；
 * 3.管理增强链。在调用链中，它会依次执行每一个增强（Advice）
 */
public class ReflectiveMethodInvocation implements MethodInvocation {
    protected final Object proxy;
    protected final Object target;
    protected final Method method;
    protected Object[] arguments;
    private Class<?> targetClass;

    protected ReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
    }

    //通过反射调用具体的方法
    public Object proceed() throws Throwable {
        return this.method.invoke(this.target, this.arguments);
    }

    public Object getProxy() {
        return proxy;
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }
}
