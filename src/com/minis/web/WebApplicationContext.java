package com.minis.web;

import javax.servlet.ServletContext;
import com.minis.context.ApplicationContext;

/**
 * 应用在web项目里的上下文接口
 * 这个上下文接口指向了 Servlet 容器本身的上下文 ServletContext。
 */
public interface WebApplicationContext extends ApplicationContext {
    String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";

    ServletContext getServletContext();
    void setServletContext(ServletContext servletContext);
}