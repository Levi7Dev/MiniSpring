package com.minis.web.servlet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 之前这些映射关系在DispatcherServlet中，
 * 现在通过 MappingRegistry 这个类来存放和管理url与对象、方法的映射关系。
 */
public class MappingRegistry {

    //保存自定义的@RequestMapping url名称列表
    private List<String> urlMappingNames = new ArrayList<>();
    //url名称与对象的映射关系
    private Map<String, Object> mappingObjs = new HashMap<>();
    //url名称与方法的映射关系
    private Map<String, Method> mappingMethods = new HashMap<>();


    public List<String> getUrlMappingNames() {
        return urlMappingNames;
    }

    public void setUrlMappingNames(List<String> urlMappingNames) {
        this.urlMappingNames = urlMappingNames;
    }

    public Map<String, Object> getMappingObjs() {
        return mappingObjs;
    }

    public void setMappingObjs(Map<String, Object> mappingObjs) {
        this.mappingObjs = mappingObjs;
    }

    public Map<String, Method> getMappingMethods() {
        return mappingMethods;
    }

    public void setMappingMethods(Map<String, Method> mappingMethods) {
        this.mappingMethods = mappingMethods;
    }
}