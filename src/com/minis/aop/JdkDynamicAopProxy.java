package com.minis.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    Object target;

    public JdkDynamicAopProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object getProxy() {
        Object obj = Proxy.newProxyInstance(JdkDynamicAopProxy.class.getClassLoader(), target.getClass().getInterfaces(), this);
        return obj;
    }

    /***
     * 当调用代理对象的某个方法时，代理对象会将调用转发给关联的InvocationHandler实现。
     * 然后，InvocationHandler的invoke方法会被调用
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("doAction")) {
            System.out.println("-----before call real object, dynamic proxy........");
            return method.invoke(target, args);
        }
        return null;
    }
}