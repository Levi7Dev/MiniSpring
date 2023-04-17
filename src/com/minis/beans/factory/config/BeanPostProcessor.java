package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;

public interface BeanPostProcessor {
    //初始化之前
    Object postProcessorBeforeInitialization(Object bean, String beanName) throws BeansException;
    //初始化之后
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

    void setBeanFactory(BeanFactory beanFactory);
}
