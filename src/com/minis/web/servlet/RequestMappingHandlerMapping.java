package com.minis.web.servlet;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;

import com.minis.web.RequestMapping;
import com.minis.web.WebApplicationContext;


public class RequestMappingHandlerMapping implements HandlerMapping {

    WebApplicationContext wac;
    private final MappingRegistry mappingRegistry = new MappingRegistry();

    public RequestMappingHandlerMapping(WebApplicationContext wac) {
        this.wac = wac;
        initMapping();
    }

    //建立URL与调用方法和实例的映射关系，存储在mappingRegistry中
    protected void initMapping() {
        Class<?> clz = null;
        Object obj = null;
        /***
         * WebApplicationContext继承了ApplicationContext，ApplicationContext继承了ListableBeanFactory，
         * ListableBeanFactory中包含getBeanDefinitionNames方法，用于获取所有BeanDefinitionNames
         */
        String[] controllerNames = this.wac.getBeanDefinitionNames();
        //扫描WAC中存放的所有bean，其中loadBeanDefinitions方法将beanID就是全类名，这样拿到beanName就可以用于实例化bean
        for (String controllerName : controllerNames) {
            try {
                clz = Class.forName(controllerName);
                obj = this.wac.getBean(controllerName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Method[] methods = clz.getDeclaredMethods();
            if (methods != null) {
                //检查每一个方法声明
                for (Method method : methods) {
                    boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                    //如果该方法带有@RequestMapping注解,则建立映射关系
                    if (isRequestMapping) {
                        String methodName = method.getName();
                        String urlmapping = method.getAnnotation(RequestMapping.class).value();
                        this.mappingRegistry.getUrlMappingNames().add(urlmapping);
                        this.mappingRegistry.getMappingObjs().put(urlmapping, obj);
                        this.mappingRegistry.getMappingMethods().put(urlmapping, method);
                    }
                }
            }
        }
    }

    //根据访问URL查找对应的调用方法
    @Override
    public HandlerMethod getHandler(HttpServletRequest request) throws Exception {
        //获取到浏览器传入的url
        String sPath = request.getServletPath();
        if (!this.mappingRegistry.getUrlMappingNames().contains(sPath)) {
            return null;
        }
        Method method = this.mappingRegistry.getMappingMethods().get(sPath);
        Object obj = this.mappingRegistry.getMappingObjs().get(sPath);
        HandlerMethod handlerMethod = new HandlerMethod(method, obj);
        return handlerMethod;
    }
}