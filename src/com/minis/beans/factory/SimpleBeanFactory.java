package com.minis.beans.factory;

import com.minis.beans.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * SimpleBeanFactory 实现了 BeanDefinitionRegistry，这样 SimpleBeanFactory 既是一个工厂同时也是一个仓库
 * SimpleBeanFactory拥有三个功能：
 *  1、单例bean的管理：该功能由SingletonBeanRegistry接口负责，DefaultSingletonBeanRegistry做了功能的具体实现。
 *  2、bean定义信息的管理：该功能由BeanDefinitionRegistry接口负责，实现对bean定义的增删等功能。
 *  3、工厂对外暴露的接口：该功能由BeanFactory接口负责，对外暴露一些方法，使得外部可以操作bean实例，比如有获取bean实例的方法。
 *
 * 外部程序只需要给SimpleBeanFactory类提供bean定义信息，具体bean的实例化由本类内部完成
 */

public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private List<String> beanDefinitionNames = new ArrayList<>();
    //存放毛坯实例，此时的bean实例并没有属性注入，用于解决循环依赖问题
    private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);

    public SimpleBeanFactory() {
    }

    //容器的核心方法
    @Override
    public Object getBean(String beanName) throws BeansException {
        //先直接从map中拿，没有则取出定义来创建，刚开始的时候是没有实例的，然后不断创建加入到容器中
        //调用的是DefaultSingletonBeanRegistry中的方法，该类存储单例bean实例并已实现属性注入，如果没有找到，则在工厂中根据bean定义信息创建bean，并添加到单例bean类中容器
        Object singleton = this.getSingleton(beanName);
        //不存在该bean则利用反射进行创建
        if (singleton == null) {
            //尝试从毛坯实例中获取
            singleton = this.earlySingletonObjects.get(beanName);
            //毛坯实例都没有则创建并注册
            if (singleton == null) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                //调用createBean方法，进而调用doCreateBean方法和handleProperties方法，从而达到将依赖的bean全部创建出来并实现属性注入
                singleton = createBean(beanDefinition);
                this.registerSingleton(beanName ,singleton);
            }
//            //找不到bean的定义信息，抛出异常
//            if (beanDefinition == null) {
//                throw new BeansException("No such bean.");
//            }
//            try {
//                singleton = Class.forName(beanDefinition.getClassName()).newInstance();
//                //实例化后注册到单例bean容器，DefaultSingletonBeanRegistry中的方法
//                this.registerSingleton(beanName, singleton);
//            } catch (Exception e) {}
        }
        return singleton;
    }

    //核心方法，createBean
    private Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        //创建毛胚bean实例
        Object obj = doCreateBean(beanDefinition);
        //存放在毛坯实例容器中
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);

        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //处理属性
        handleProperties(beanDefinition, clz, obj);

        return obj;
    }

    //创建毛胚bean实例，仅仅调用构造方法，没有进行属性处理
    private Object doCreateBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;
        try {
            clz = Class.forName(bd.getClassName());
            //处理构造器参数
            ArgumentValues argumentValues = bd.getConstructorArgumentValues();
            if (!argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues = new Object[argumentValues.getArgumentCount()];
                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
                    ArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                    //System.out.println("++++++:" + argumentValue.getType());
                    if ("String".equals(argumentValue.getType()) || "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    } else if ("Integer".equals(argumentValue.getType()) || "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    } else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue()).intValue();
                    } else {
                        //其余基本数据类型省略，默认为string
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                }
                // 根据参数类型实例化
                try {
                    //传入构造函数的参数类型，注意对应的bean类需要有对应的构造函数
                    con = clz.getConstructor(paramTypes);
                    //传入具体参数
                    obj = con.newInstance(paramValues);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                //构造器参数为空则直接实例化
                obj = clz.newInstance();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(bd.getId() + " bean created. " + bd.getClassName() + " : " + obj.toString());
        return obj;
    }

    //setter注入，处理属性
    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        System.out.println("handle properties for bean : " + bd.getId());

        PropertyValues propertyValues = bd.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            for (int i = 0; i < propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                String pType = propertyValue.getType();
                String pName = propertyValue.getName();
                Object pValue = propertyValue.getValue();
                boolean isRef = propertyValue.getIsRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];

                //不是引用，只是普通数据类型
                if (!isRef) {
                    if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                        paramTypes[0] = String.class;
                    } else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                        paramTypes[0] = Integer.class;
                    } else if ("int".equals(pType)) {
                        paramTypes[0] = int.class;
                    } else {
                        paramTypes[0] = String.class;
                    }
                    paramValues[0] = pValue;

                } else {
                    //是引用，创建独立的bean
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    //调用getBean创建ref的bean实例
                    try {
                        paramValues[0] = getBean((String)pValue);
                    } catch (BeansException e) {
                        e.printStackTrace();
                    }
                }

                //按照setXxxx规范查找setter方法，调用setter方法设置属性
                String methodName = "set" + pName.substring(0, 1).toUpperCase() + pName.substring(1);
                Method method = null;
                try {
                    //通过相应的类文件拿到对应的方法
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                try {
                    //调用方法，并将参数paramValues传入
                    method.invoke(obj, paramValues);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //对所有的Bean调用了一次getBean()，
    //利用getBean()方法中的createBean()创建Bean实例，就可以只用一个方法把容器中所有的Bean的实例创建出来。
    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean containsBean(String beanName) {
        return containsSingleton(beanName);
    }

    //以下几个是BeanFactory中的方法
    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }


    //以下四个方法是BeanDefinitionRegistry接口中的方法
    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
        //是懒加载则只是将bean的定义放在容器中，而不是直接创建bean
        if (!beanDefinition.isLazyInit()) {
            try {
                //不是懒加载会直接将bean创建出来
                getBean(name);
            } catch (Exception e) {}
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        //DefaultSingletonBeanRegistry中的方法
        //bean的定义删除后对应的bean实例也应该删除
        this.removeSingleton(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }
}
