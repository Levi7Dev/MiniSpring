package com.minis.beans;

import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.SimpleBeanFactory;
import com.minis.beans.factory.xml.XmlBeanDefinitionReader;
import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;


public class ClassPathXmlApplicationContext implements BeanFactory {
    SimpleBeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String fileName) {
        Resource resource = new ClassPathXmlResource(fileName);
        SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        this.beanFactory = beanFactory;
    }

    //对外暴露的方法，让外部程序从容器中获取Bean实例，会逐步演化
    public Object getBean(String beanName) throws BeansException {
        //根据bean的名字获取实例
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public void registerBean(String beanName, Object obj) {
        this.beanFactory.registerBean(beanName, obj);
    }

    @Override
    public Boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }


    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanFactory.registerBeanDefinition(beanDefinition);
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

