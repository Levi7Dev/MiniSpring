package com.minis.beans.factory;

import com.minis.beans.BeanDefinition;
import com.minis.beans.BeansException;

public interface BeanFactory {
    Object getBean(String beanName) throws BeansException;
    void registerBean(String beanName, Object obj);
    Boolean containsBean(String name);
}