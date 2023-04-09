package com.minis.beans.factory.xml;

import com.minis.beans.BeanDefinition;
import com.minis.beans.SingletonBeanRegistry;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.SimpleBeanFactory;
import com.minis.core.Resource;
import org.dom4j.Element;

public class XmlBeanDefinitionReader {
    SimpleBeanFactory simpleBeanFactory;
    public XmlBeanDefinitionReader(SimpleBeanFactory simpleBeanFactory) {
        this.simpleBeanFactory = simpleBeanFactory;
    }
    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanID = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);
            //这个beanID对应的就是bean的name
            this.simpleBeanFactory.registerBeanDefinition(beanID, beanDefinition);
        }
    }
}
