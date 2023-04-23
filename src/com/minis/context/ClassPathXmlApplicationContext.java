package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.AbstractAutowireCapableBeanFactory;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.beans.factory.xml.XmlBeanDefinitionReader;
import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;

import java.util.ArrayList;
import java.util.List;


public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    //SimpleBeanFactory beanFactory;
    //AbstractAutowireCapableBeanFactory beanFactory;
    DefaultListableBeanFactory beanFactory;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();


    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }

    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        //获取配置文件信息，里面包含了各种元素
        Resource resource = new ClassPathXmlResource(fileName);
        //SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        //AbstractAutowireCapableBeanFactory bf = new AbstractAutowireCapableBeanFactory();
        //DefaultListableBeanFactory工厂实现了所有工厂接口的方法，是bean工厂的默认实现
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
        //将xml文件中的bean定义信息添加到beanFactory中
        reader.loadBeanDefinitions(resource);
        this.beanFactory = bf;
        if (isRefresh) {
            try {
                //此时beanFactory中已经包含了从xml文件中读取的bean定义信息
                //核心方法，对于实现了 BeanPostProcessor 接口的 Bean，Spring 会将其注册到 beanPostProcessors 列表中
                refresh();
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    //对外暴露的方法，让外部程序从容器中获取Bean实例，会逐步演化
    public Object getBean(String beanName) throws BeansException {
        //根据bean的名字获取实例
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

    //父类抽象方法的实现
    @Override
    void registerListeners() {
        ApplicationListener listener = new ApplicationListener();
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    void initApplicationEventPublisher() {
        ApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }

    //refresh函数中调用
    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory bf) {

    }

    @Override
    void registerBeanPostProcessors(ConfigurableListableBeanFactory bf) {
        //为了获取spring中管理bean，会new ClassPathXmlApplicationContext()，然后再创建BeanFactory，
        //需要把这个工厂注入到注解处理器中，bean定义信息也是在同一个bean工厂中
        //达到了同一个bean工厂，既有bean的定义信息，也为该工厂注册了注解处理器，
        //那么该工厂同时拥有了从xml文件中构造bean实例的能力和从注解中注入bean实例的能力
        //为该工厂注册一个Autowired注解注解处理器，
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }

//    private void registerBeanPostProcessors(AbstractAutowireCapableBeanFactory bf) {
//        //为了获取spring中管理bean，会new ClassPathXmlApplicationContext()，然后再创建BeanFactory，
//        //需要把这个工厂注入到注解处理器中，bean定义信息也是在同一个bean工厂中
//        //达到了同一个bean工厂，既有bean的定义信息，也为该工厂注册了注解处理器，
//        //那么该工厂同时拥有了从xml文件中构造bean实例的能力和从注解中构造bean实例的能力
//        //为该工厂注册一个Autowired注解注解处理器，
//        bf.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
//    }

    @Override
    public void onRefresh() {
        //bean工厂中已经注册了bean定义信息，调用工厂中的refresh函数会为每个bena实例并初始化
        this.beanFactory.refresh();
    }

    @Override
    void finishRefresh() {
        publishEvent(new ContextRefreshEvent("Context Refreshed..."));
    }

    //ApplicationEventPublisher中的方法
    @Override
    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }
}

