package com.minis.beans.factory;

/**
 * 当一个类实现了 BeanFactoryAware 接口时，它可以获得对 Spring BeanFactory 的引用。
 * 这意味着该类可以访问和操作 Spring 容器中的其他 Bean。
 * BeanFactoryAware 接口只有一个方法需要实现，即 setBeanFactory(BeanFactory beanFactory)。
 */
public interface BeanFactoryAware {
    void setBeanFactory(BeanFactory beanFactory);
}
