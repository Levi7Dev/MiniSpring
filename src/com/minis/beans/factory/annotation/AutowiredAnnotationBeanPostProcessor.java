package com.minis.beans.factory.annotation;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.AbstractAutowireCapableBeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/***
 * Autowired注解处理类
 */
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    //private AbstractAutowireCapableBeanFactory beanFactory;
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //获取该bean下的所有被注解的成员变量
        Object result = bean;
        Class<?> clazz = bean.getClass();
        //bean下的所有字段
        Field[] fields = clazz.getDeclaredFields();

        if (fields != null) {
            //对bean类下每一个属性进行判断，如果带有@Autowired注解则进行处理
            for (Field field : fields) {
                boolean isAutowired = field.isAnnotationPresent(Autowired.class);
                if (isAutowired) {
                    //如果属性bbs上使用了@Autowired，bbs对应的类应该在xml文件中声明，并且bean的id也应该为bbs，
                    //这样在读取xml文件时，会以bbs为beanID，类全路径名作为beanClassName，并把这些信息注册到BeanFactory中
                    //根据属性名查找同名的bean（这点很重要，要与xml文件中的id值相同），会根据属性变量名字去查找对应的属性bean class，
                    String fieldName = field.getName();
                    //beanFactory要提前赋值
                    Object autowiredObj = this.getBeanFactory().getBean(fieldName);
                    //设置属性，完成注入
                    try {
                        field.setAccessible(true);
                        field.set(bean, autowiredObj);
                        System.out.println("autowire " + fieldName + " for bean " + beanName);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

}
