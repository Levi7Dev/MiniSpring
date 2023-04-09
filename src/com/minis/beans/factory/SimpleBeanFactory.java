package com.minis.beans.factory;

import com.minis.beans.BeanDefinition;
import com.minis.beans.BeansException;
import com.minis.beans.DefaultSingletonBeanRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    //private List<BeanDefinition> beanDefinitions = new ArrayList<>();
    //private List<String> beanNames = new ArrayList<>();
    //private Map<String, Object> singletons = new HashMap<>();

    private Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();

    public SimpleBeanFactory() {
    }

    //容器的核心方法
    @Override
    public Object getBean(String beanName) throws BeansException {
        //先直接从map中拿，没有则取出定义来创建，刚开始的时候是没有实例的，然后不断创建加入到容器中
        //调用的是父类DefaultSingletonBeanRegistry中的方法
        Object singleton = this.getSingleton(beanName);
        //不存在该bean则利用反射进行创建
        if (singleton == null) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanName);
            //找不到bean的定义信息，抛出异常
            if (beanDefinition == null) {
                throw new BeansException("No such bean.");
            }
            try {
                singleton = Class.forName(beanDefinition.getClassName()).newInstance();
                //实例化后注册到容器中
                this.registerSingleton(beanName, singleton);
            } catch (Exception e) {}
        }
        return singleton;
    }

    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitions.put(beanDefinition.getId(), beanDefinition);
    }

    public Boolean containsBean(String beanName) {
        return containsSingleton(beanName);
    }

    //BeanFactory中的方法
    @Override
    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }
}
