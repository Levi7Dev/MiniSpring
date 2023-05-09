package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.FactoryBean;

//aop相关功能
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry{

    protected Class<?> getTypeForFactoryBean(final FactoryBean<?> factoryBean) {
        return factoryBean.getObjectType();
    }

    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName) {
        Object object = doGetObjectFromFactoryBean(factory, beanName);
        try {
            object = postProcessObjectFromFactoryBean(object, beanName);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return object;
    }

    private Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    //从factory bean中获取内部包含的target对象
    private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName) {
        Object object = null;
        try {
            object = factory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    protected Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        if(!(beanInstance instanceof FactoryBean)){
            return beanInstance;
        }
        //beanInstance已经是ProxyFactoryBean类型，此处向上转型了
        FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
        return getObjectFromFactoryBean(factory,beanName);
    }
}