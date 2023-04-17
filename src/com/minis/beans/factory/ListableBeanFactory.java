package com.minis.beans.factory;

import com.minis.beans.BeansException;

import java.util.Map;

/***
 * 扩展了 BeanFactory 接口，提供了更多关于 Bean 定义和访问的方法。
 * ListableBeanFactory 主要用于处理和查询 Bean 定义的各种信息，包括 Bean 名称、别名、类型等。
 *
 * ListableBeanFactory 接口的实现通常用于需要处理大量 Bean 定义的场景，
 * 例如在企业级应用中。通过这个接口，开发人员可以方便地查询和管理 Bean 定义，从而更好地控制和优化应用的运行。
 * 在 Spring 中，DefaultListableBeanFactory 是一个常用的 ListableBeanFactory 实现，
 * 它同时实现了其他功能，如自动装配和依赖注入。
 */

public interface ListableBeanFactory extends BeanFactory{

    boolean containsBeanDefinition(String beanName);

    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    String[] getBeanNamesForType(Class<?> type);

    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

}
