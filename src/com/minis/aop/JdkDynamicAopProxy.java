package com.minis.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    Object target;
    PointcutAdvisor advisor;

    public JdkDynamicAopProxy(Object target, PointcutAdvisor advisor) {
        this.target = target;
        this.advisor = advisor;
    }

    //AopProxy接口方法
    @Override
    public Object getProxy() {
        //返回一个指定接口的代理对象，这个对象将方法调用委托给提供的InvocationHandler实现
        Object obj = Proxy.newProxyInstance(JdkDynamicAopProxy.class.getClassLoader(), target.getClass().getInterfaces(), this);
        return obj;
    }

    /***
     * 当调用代理对象的某个方法时，代理对象会将调用转发给关联的InvocationHandler实现。
     * 然后，InvocationHandler的invoke方法会被调用
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = (target != null ? target.getClass() : null);
        if (this.advisor.getPointcut().getMethodMatcher().matches(method, targetClass)) {
            MethodInterceptor interceptor = this.advisor.getMethodInterceptor();
            ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass);
            //调用具体的invoke方法
            return interceptor.invoke(invocation);
        }
        return null;
    }
}