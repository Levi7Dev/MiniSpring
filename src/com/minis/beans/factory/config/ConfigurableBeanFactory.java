package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;

/***
 * ConfigurableBeanFactory 主要关注的是 Bean 的生命周期和作用域管理，以及对 BeanFactory 的配置和自定义。
 *
 * 维护 Bean 之间的依赖关系以及支持 Bean 处理器
 */
public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry {

    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    int getBeanPostProcessorCount();

    void registerDependentBean(String beanName, String dependentBeanName);

    String[] getDependentBeans(String beanName);

    String[] getDependenciesForBean(String beanName);

}
