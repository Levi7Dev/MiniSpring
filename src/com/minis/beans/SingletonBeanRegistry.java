package com.minis.beans;

public interface SingletonBeanRegistry {
    //单例bean的注册，获取，判断是否存在，获取所有的单例bean
    void registerSingleton(String beanName, Object singletonObject);
    Object getSingleton(String beanName);
    boolean containsSingleton(String beanName);
    String[] getSingletonNames();
}
