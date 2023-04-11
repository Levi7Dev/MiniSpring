package com.minis.beans.factory.xml;

import com.minis.beans.*;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;
import com.minis.beans.factory.support.AbstractBeanFactory;
import com.minis.beans.factory.support.SimpleBeanFactory;
import com.minis.core.Resource;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class XmlBeanDefinitionReader {

    AbstractBeanFactory bf;

    public XmlBeanDefinitionReader(AbstractBeanFactory bf) {
        this.bf =  bf;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanID = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            String initMethodName = element.attributeValue("init-method");
            BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);

            //解析 <property> 和 <constructor-arg> 两个标签
            //处理属性，解析 <property>
            List<Element> propertyElements = element.elements("property");
            PropertyValues PVS = new PropertyValues();
            //存放引用，一个xml中存在多个
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                String pRef = e.attributeValue("ref");
                //用于存放pValue或者pRef值，具体取决于哪个存在
                String pV = "";
                boolean isRef = false;
                //如果是引用，在xml文件中就不会有value字段，value值不为空则说明不是引用类型
                if (pValue != null && !pValue.equals("")) {
                    isRef = false;
                    pV = pValue;
                } else if (pRef != null && !pRef.equals("")) {
                    //引用字段有值，说明是引用类型
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                PVS.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
            }
            beanDefinition.setPropertyValues(PVS);
            String[] refArray = refs.toArray(new String[0]);
            //dependsOn数组存放本类需要的引用（类）
            beanDefinition.setDependsOn(refArray);
            beanDefinition.setInitMethodName(initMethodName);

            //处理构造器参数，解析<constructor-arg>
            List<Element> constructorElements = element.elements("constructor-arg");
            ConstructorArgumentValues AVS = new ConstructorArgumentValues();
            for (Element e : constructorElements) {
                String aType = e.attributeValue("type");
                String aName = e.attributeValue("name");
                String aValue = e.attributeValue("value");
                AVS.addArgumentValue(new ConstructorArgumentValue(aType, aName, aValue));
            }
            beanDefinition.setConstructorArgumentValues(AVS);

            //这个beanID对应的就是bean的name
            this.bf.registerBeanDefinition(beanID, beanDefinition);
        }
    }
}
