package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.FactoryBean;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConfigurableBeanFactory;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 代码复用，解耦
 */
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory,BeanDefinitionRegistry {

    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    protected List<String> beanDefinitionNames = new ArrayList<>();
    //存放毛坯实例，此时的bean实例并没有属性注入，用于解决循环依赖问题
    protected final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);

    public AbstractBeanFactory() {
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
                if (beanDefinition != null) {
                    //调用createBean方法，进而调用doCreateBean方法和handleProperties方法，从而达到将依赖的bean全部创建出来并实现属性注入
                    singleton = createBean(beanDefinition);
                    this.registerSingleton(beanName ,singleton);

                    //aop：ProxyFactoryBean实现了BeanFactoryAware接口，注入的时候把beanFactory set进去
                    if (singleton instanceof BeanFactoryAware) {
                        ((BeanFactoryAware) singleton).setBeanFactory(this);
                    }

                    //beanpostprocessor，前者在类初始化前调用，后者在类初始化之后调用。
                    //step 1 : postProcessBeforeInitialization
                    applyBeanPostProcessorsBeforeInitialization(singleton, beanName);

                    //step 2 : init-method
                    if (beanDefinition.getInitMethodName() != null && !beanDefinition.getInitMethodName().equals("")) {
                        invokeInitMethod(beanDefinition, singleton);
                    }

                    //step 3 : postProcessAfterInitialization
                    applyBeanPostProcessorsAfterInitialization(singleton, beanName);
                } else {
                    return  null;
                }
            }
        }
        //aop
        // 通过 AbstractBeanFactory 获取 Bean 的时候，对 FactoryBean 进行了特殊处理，
        // 获取到的已经不是 FactoryBean 本身了，而是它内部包含的那一个对象。
        if (singleton instanceof FactoryBean) {
            return this.getObjectForBeanInstance(singleton, beanName);
        }

        if (singleton == null) {
            throw new BeansException("bean is null.");
        }
        return singleton;
    }

    private void invokeInitMethod(BeanDefinition bd, Object obj) {
        Class<?> clz = obj.getClass();
        Method method = null;
        try {
            method = clz.getMethod(bd.getInitMethodName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        try {
            method.invoke(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //核心方法，createBean
    private Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        //创建毛胚bean实例
        Object obj = doCreateBean(beanDefinition);
        //存放在毛胚实例容器中
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);

        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //处理属性
        populateBean(beanDefinition, clz, obj);

        return obj;
    }

    //创建毛胚bean实例，仅仅调用构造方法，没有进行属性处理
    private Object doCreateBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;
        try {
            //找到指定的类，根据
            //System.out.println("+++++++:" + bd.getClassName());
            clz = Class.forName(bd.getClassName());
            //System.out.println("-------:" + clz.toString());
            //处理构造器参数
            ConstructorArgumentValues constructorArgumentValues = bd.getConstructorArgumentValues();
            if (constructorArgumentValues != null) {
                if (!constructorArgumentValues.isEmpty()) {
                    Class<?>[] paramTypes = new Class<?>[constructorArgumentValues.getArgumentCount()];
                    Object[] paramValues = new Object[constructorArgumentValues.getArgumentCount()];
                    for (int i = 0; i < constructorArgumentValues.getArgumentCount(); i++) {
                        ConstructorArgumentValue constructorArgumentValue = constructorArgumentValues.getIndexedArgumentValue(i);
                        //System.out.println("++++++:" + argumentValue.getType());
                        if ("String".equals(constructorArgumentValue.getType()) || "java.lang.String".equals(constructorArgumentValue.getType())) {
                            paramTypes[i] = String.class;
                            paramValues[i] = constructorArgumentValue.getValue();
                        } else if ("Integer".equals(constructorArgumentValue.getType()) || "java.lang.Integer".equals(constructorArgumentValue.getType())) {
                            paramTypes[i] = Integer.class;
                            paramValues[i] = Integer.valueOf((String) constructorArgumentValue.getValue());
                        } else if ("int".equals(constructorArgumentValue.getType())) {
                            paramTypes[i] = int.class;
                            paramValues[i] = Integer.valueOf((String) constructorArgumentValue.getValue()).intValue();
                        } else {
                            //其余基本数据类型省略，默认为string
                            paramTypes[i] = String.class;
                            paramValues[i] = constructorArgumentValue.getValue();
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
                    //构造器参数为空则直接使用无参构造函数实例化，对应的类需要实现无参构造器
                    obj = clz.newInstance();
                }
            } else {
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

    private void populateBean(BeanDefinition bd, Class<?> clz, Object obj) {
        handleProperties(bd, clz, obj);
    }

    //setter注入，处理属性
    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        System.out.println("handle properties for bean : " + bd.getId());
        System.out.println("----------------------------");

        PropertyValues propertyValues = bd.getPropertyValues();
        if (propertyValues != null) {
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
                        //不是引用存放实际的值
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
                            //是引用，存放对应的bean实例
                            paramValues[0] = getBean((String)pValue);
                        } catch (BeansException e) {
                            e.printStackTrace();
                        }
                    }

                    //按照setXxxx规范查找setter方法，调用setter方法设置属性
                    String methodName = "set" + pName.substring(0, 1).toUpperCase() + pName.substring(1);
                    Method method = null;
                    try {
                        //通过相应的类文件拿到对应的setter方法
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
    }

    @Override
    public Boolean containsBean(String name) {
        return this.containsSingleton(name);
    }

    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition bd) {
        this.beanDefinitionMap.put(name, bd);
        this.beanDefinitionNames.add(name);
        if (!bd.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
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

    //AbstractAutowireCapableBeanFactory类实现方法
    //bean初始化之前执行
    abstract public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException;
    //bean初始化之后执行
    abstract public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException;
}
