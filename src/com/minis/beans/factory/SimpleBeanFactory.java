package com.minis.beans.factory;

import com.minis.beans.BeanDefinition;
import com.minis.beans.BeanDefinitionRegistry;
import com.minis.beans.BeansException;
import com.minis.beans.DefaultSingletonBeanRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * SimpleBeanFactory 实现了 BeanDefinitionRegistry，这样 SimpleBeanFactory 既是一个工厂同时也是一个仓库
 * SimpleBeanFactory拥有三个功能：
 *  1、单例bean的管理：该功能由SingletonBeanRegistry接口负责，DefaultSingletonBeanRegistry做了功能的具体实现。
 *  2、bean定义信息的管理：该功能由BeanDefinitionRegistry接口负责，实现对bean定义的增删等功能。
 *  3、工厂对外暴露的接口：该功能由BeanFactory接口负责，对外暴露一些方法，使得外部可以操作bean实例，比如有获取bean实例的方法。
 *
 * 外部程序只需要给SimpleBeanFactory类提供bean定义信息，具体bean的实例化由本类内部完成
 */

public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private List<String> beanDefinitionNames = new ArrayList<>();

    public SimpleBeanFactory() {
    }

    //容器的核心方法
    @Override
    public Object getBean(String beanName) throws BeansException {
        //先直接从map中拿，没有则取出定义来创建，刚开始的时候是没有实例的，然后不断创建加入到容器中
        //调用的是DefaultSingletonBeanRegistry中的方法，该类存储单例bean实例，如果没有找到，则在工厂中根据bean定义信息创建bean，并添加到单例bean类中容器
        Object singleton = this.getSingleton(beanName);
        //不存在该bean则利用反射进行创建
        if (singleton == null) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            //找不到bean的定义信息，抛出异常
            if (beanDefinition == null) {
                throw new BeansException("No such bean.");
            }
            try {
                singleton = Class.forName(beanDefinition.getClassName()).newInstance();
                //实例化后注册到单例bean容器，DefaultSingletonBeanRegistry中的方法
                this.registerSingleton(beanName, singleton);
            } catch (Exception e) {}
        }
        return singleton;
    }

    public Boolean containsBean(String beanName) {
        return containsSingleton(beanName);
    }

    //以下几个是BeanFactory中的方法
    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }


    //以下四个方法是BeanDefinitionRegistry接口中的方法
    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
        //是懒加载则只是将bean的定义放在容器中，而不是直接创建bean
        if (!beanDefinition.isLazyInit()) {
            try {
                //不是懒加载会直接将bean创建出来
                getBean(name);
            } catch (Exception e) {}
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        //DefaultSingletonBeanRegistry中的方法
        //bean的定义删除后对应的bean实例也应该删除
        this.removeSingleton(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }
}
