package com.minis.aop.framework.autoproxy;

import com.minis.aop.AopProxyFactory;
import com.minis.aop.DefaultAopProxyFactory;
import com.minis.aop.PointcutAdvisor;
import com.minis.aop.ProxyFactoryBean;
import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.util.PatternMatchUtils;

public class BeanNameAutoProxyCreator implements BeanPostProcessor {
    String pattern; //代理对象名称模式，如action*
    private BeanFactory beanFactory;
    private AopProxyFactory aopProxyFactory;
    private String interceptorName;
    private PointcutAdvisor advisor;

    public BeanNameAutoProxyCreator() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    //核心方法。在bean实例化之后，init-method调用之前执行这个步骤。
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (isMatch(beanName, this.pattern)) {
            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean(); //创建ProxyFactoryBean
            proxyFactoryBean.setTarget(bean);
            proxyFactoryBean.setBeanFactory(beanFactory);
            proxyFactoryBean.setAopProxyFactory(aopProxyFactory);
            proxyFactoryBean.setInterceptorName(interceptorName);
            return proxyFactoryBean;
        } else {
            return bean;
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    protected boolean isMatch(String beanName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, beanName);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public AopProxyFactory getAopProxyFactory() {
        return aopProxyFactory;
    }

    public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
        this.aopProxyFactory = aopProxyFactory;
    }

    public String getInterceptorName() {
        return interceptorName;
    }

    public void setInterceptorName(String interceptorName) {
        this.interceptorName = interceptorName;
    }

    public PointcutAdvisor getAdvisor() {
        return advisor;
    }

    public void setAdvisor(PointcutAdvisor advisor) {
        this.advisor = advisor;
    }
}