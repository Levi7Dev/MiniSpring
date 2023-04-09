package com.minis.beans.factory;

import com.minis.beans.BeansException;

public interface BeanFactory {
    Object getBean(String beanName) throws BeansException;
    Boolean containsBean(String name);
    //交给BeanDefinitionRegistry管理，那么该方法可以取消
    //void registerBean(String beanName, Object obj);

    boolean isSingleton(String name);
    boolean isPrototype(String name);
    Class<?> getType(String name);
}