package com.minis.beans;

/***
 * 集中管理BeanDefinition，相当于一个BeanDefinition仓库
 * 原来BeanDefinition是存放在BeanFactory中，现在做了修改，那么BeanFactory也需要做出修改
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String name, BeanDefinition bd);
    void removeBeanDefinition(String name);
    BeanDefinition getBeanDefinition(String name);
    boolean containsBeanDefinition(String name);
}