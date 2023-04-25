package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory {

    ConfigurableListableBeanFactory parentBeanFactory;


    @Override
    public Object getBean(String beanName) throws BeansException {
        Object result = super.getBean(beanName);
        if (result == null) {
            result = this.parentBeanFactory.getBean(beanName);
        }
        return result;
    }

    @Override
    public int getBeanDefinitionCount() {
        //AbstractBeanFactory类中成员变量
        return this.beanDefinitionMap.size();
    }

    //直接拿到父类AbstractBeanFactory中变量beanDefinitionNames，也就是整个容器中的bean定义信息
    @Override
    public String[] getBeanDefinitionNames() {
        return (String[]) this.beanDefinitionNames.toArray(new String[this.beanDefinitionNames.size()]);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        List<String> result = new ArrayList<>();
        for (String beanName : this.beanDefinitionNames) {
            boolean matchFound = false;
            BeanDefinition mbd = this.getBeanDefinition(beanName);
            Class<?> classToMatch = mbd.getClass();
            //匹配成功
            if (type.isAssignableFrom(classToMatch)) {
                matchFound = true;
            } else {
                matchFound = false;
            }
            if (matchFound) {
                result.add(beanName);
            }
        }
        return (String[]) result.toArray();
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        String[] beanNames = getBeanNamesForType(type);
        Map<String, T> result = new LinkedHashMap<>(beanNames.length);
        for (String beanName : beanNames) {
            //AbstractBeanFactory类中方法
            Object beanInstance = this.getBean(beanName);
            result.put(beanName, (T)beanInstance);
        }
        return result;
    }

    public void setParent(ConfigurableListableBeanFactory beanFactory) {
        this.parentBeanFactory = beanFactory;
    }
}















