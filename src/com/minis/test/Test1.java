package com.minis.test;

import com.minis.beans.ClassPathXmlApplicationContext;
import com.minis.beans.BeansException;

public class Test1 {
    public static void main(String[] args) throws BeansException {
        //配置文件应放在resources目录下，（新建一个resources——>右键Mark directory as——>Resources root）
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        AService aservice1 = (AService) ctx.getBean("aservice");
        aservice1.sayHello();

//        //重构后的代码
//        //获取到配置文件的bean信息
//        ClassPathXmlResource classPathXmlResource = new ClassPathXmlResource("beans.xml");
//
//        BeanFactory beanFactory = new SimpleBeanFactory();
//        //读取bean的定义，并将BeanDefinition加载到beanFactory中
//        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
//        //加载配置文件，并将bean定义实例加载到beanFactory中
//        xmlBeanDefinitionReader.loadBeanDefinitions(classPathXmlResource);
//        //最终的bean是从beanFactory中获取
//        AService aservice = (AService)beanFactory.getBean("aservice");
//        aservice.sayHello();
    }
}
