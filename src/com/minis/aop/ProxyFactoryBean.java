package com.minis.aop;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.FactoryBean;
import com.minis.util.ClassUtils;

public class ProxyFactoryBean implements FactoryBean<Object>, BeanFactoryAware {
    private AopProxyFactory aopProxyFactory;
    private String[] interceptorNames;
    private String targetName;
    private Object target;
    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private Object singletonInstance;

    //beanFactory没有获取具体实例，需要在AbstractBeanFactory类中的getBean方法set进来
    //具体实现方式是本类实现BeanFactoryAware接口
    private BeanFactory beanFactory;
    private String interceptorName;
    private PointcutAdvisor advisor;

    public ProxyFactoryBean() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    private synchronized void initializeAdvisor() {
        Object advice = null;
        //方法拦截器
        MethodInterceptor mi = null;
        try {
            //beanFactory在AbstractBeanFactory类中的getBean方法set进来，不会出现空指针异常
            //获取到具体增强的bean，包含增强的方法
            advice = this.beanFactory.getBean(this.interceptorName);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        this.advisor = (PointcutAdvisor) advice;
    }

    //set注入
    public void setInterceptorName(String interceptorName) {
        this.interceptorName = interceptorName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
        this.aopProxyFactory = aopProxyFactory;
    }

    public AopProxyFactory getAopProxyFactory() {
        return this.aopProxyFactory;
    }

    protected AopProxy createAopProxy() {
        return getAopProxyFactory().createAopProxy(target, this.advisor);
    }

    public void setInterceptorNames(String... interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    //获取内部对象target，并将target生成代理对象返回
    @Override
    public Object getObject() throws Exception {
        initializeAdvisor();
        return getSingletonInstance();
    }

    private synchronized Object getSingletonInstance() {//获取代理
        if (this.singletonInstance == null) {
            this.singletonInstance = getProxy(createAopProxy());
        }
        return this.singletonInstance;
    }

    protected Object getProxy(AopProxy aopProxy) {//生成代理对象
        //最终会调用JdkDynamicAopProxy中的getProxy方法，返回一个代理对象
        return aopProxy.getProxy();
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }
}
