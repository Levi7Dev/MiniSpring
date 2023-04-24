package com.minis.web;

import javax.servlet.ServletContext;
import com.minis.context.ClassPathXmlApplicationContext;

/***
 * 继承了ClassPathXmlApplicationContext类，具备了ioc的功能
 */
public class AnnotationConfigWebApplicationContext extends ClassPathXmlApplicationContext implements WebApplicationContext{
    //会传入tomcat创建的全局ServletContext
    private ServletContext servletContext;

    public AnnotationConfigWebApplicationContext(String fileName) {
        super(fileName);
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}