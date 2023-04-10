package com.minis.context;

import com.minis.beans.BeanDefinition;
import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.SimpleBeanFactory;
import com.minis.beans.factory.xml.XmlBeanDefinitionReader;
import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;


public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher{

    SimpleBeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }

    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        //获取配置文件信息，里面包含了各种元素
        Resource resource = new ClassPathXmlResource(fileName);
        SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        //将资源中的bean定义信息添加到beanFactory中，beanFactory中只包含bean的定义信息
        reader.loadBeanDefinitions(resource);
        this.beanFactory = beanFactory;
        if (!isRefresh) {
            this.beanFactory.refresh();
        }
    }

    //对外暴露的方法，让外部程序从容器中获取Bean实例，会逐步演化
    public Object getBean(String beanName) throws BeansException {
        //根据bean的名字获取实例
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public boolean isSingleton(String name) {
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }

    @Override
    public Boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }

    //ApplicationEventPublisher中的方法
    @Override
    public void publishEvent(ApplicationEvent event) {

    }
}












//    private void readXml(String fileName) {
//        SAXReader saxReader = new SAXReader();
//        try {
//            URL xmlPath = this.getClass().getClassLoader().getResource(fileName);
//            Document document = saxReader.read(xmlPath);
//            Element rootElement = document.getRootElement();
//            //对配置文件的每个bean进行处理
//            List<Element> list = rootElement.elements();
//            for (Element element : list) {
//                //获取bean的基本信息
//                String beanID = element.attributeValue("id");
//                String beanClassName = element.attributeValue("class");
//                BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);
//                //获取到一个bean，添加到列表中
//                beanDefinitions.add(beanDefinition);
//            }
//        } catch (Exception e) {
//
//        }
//    }

    //利用反射创建Bean实例，并存储在singletons中
    //注意beanDefinition与bean不同，bean是具体的类对象，而beanDefinition是对xml中bean属性的定义
//    private void instanceBeans() {
//        for (BeanDefinition beanDefinition : beanDefinitions) {
//            try {
//                singletons.put(beanDefinition.getId(), Class.forName(beanDefinition.getClassName()).newInstance());
//            } catch (Exception e) {
//
//            }
//        }
//    }

