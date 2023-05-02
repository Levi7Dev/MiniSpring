package com.minis.web;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.minis.beans.PropertyEditor;
import com.minis.beans.PropertyValues;
import com.minis.util.WebUtils;

/***
 * 这个类代表的是一个内部的目标对象，用于将 Request 请求内的字符串参数转换成不同类型的参数，来进行适配。
 */
public class WebDataBinder {
    private Object target;
    private Class<?> clz;
    private String objectName;

    public WebDataBinder(Object target) {
        this(target, "");
    }

    public WebDataBinder(Object target, String targetName) {
        this.target = target;
        this.objectName = targetName;
        this.clz = this.target.getClass();
    }

    //核心绑定方法，将request里面的参数值绑定到目标对象的属性上
    public void bind(HttpServletRequest request) {
        PropertyValues mpvs = assignParameters(request);
        addBindValues(mpvs, request);
        doBind(mpvs);
    }

    private void doBind(PropertyValues mpvs) {
        applyPropertyValues(mpvs);
    }

    //实际将参数值与对象属性进行绑定的方法
    protected void applyPropertyValues(PropertyValues mpvs) {
        getPropertyAccessor().setPropertyValues(mpvs);
    }

    //设置属性值的工具
    protected BeanWrapperImpl getPropertyAccessor() {
        return new BeanWrapperImpl(this.target);
    }

    //把 Request 里的参数换成内存里的一个 map 对象，这一步用到了底层的 WebUtils 工具类
    private PropertyValues assignParameters(HttpServletRequest request) {
        Map<String, Object> map = WebUtils.getParametersStartingWith(request, "");
        return new PropertyValues(map);
    }

    //注册自定义的editor
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        getPropertyAccessor().registerCustomEditor(requiredType, propertyEditor);
    }

    protected void addBindValues(PropertyValues mpvs, HttpServletRequest request) {
    }
}