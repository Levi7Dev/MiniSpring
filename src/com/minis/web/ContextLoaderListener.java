package com.minis.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/***
 * 用于启动ioc容器，在web.xml文件中声明的listener会指向该类
 */
public class ContextLoaderListener implements ServletContextListener {
    //代表配置文件路径的一个变量，也就是 IoC 容器的配置文件
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
    private WebApplicationContext webApplicationContext;


    public ContextLoaderListener() {
    }

    public ContextLoaderListener(WebApplicationContext context) {
        this.webApplicationContext = context;
    }


    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    /***
     * 初始化方法，自动调用
     * 当 Sevlet 服务器启动时，Listener 会优先启动，读配置文件路径，启动过程中初始化上下文，
     * 然后启动 IoC 容器，体现在new AnnotationConfigWebApplicationContext(sContextLocation)这条语句（被调用的函数中），
     * 这个容器通过 refresh() 方法加载所管理的 Bean 对象。
     * 这样就实现了 Tomcat 启动的时候同时启动 IoC 容器。
     * @param event
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        //获取到的是tomcat创建的全局ServletContext
        initWebApplicationContext(event.getServletContext());
    }

    /***
     * 在这段代码中，通过配置文件参数从 web.xml 中得到配置文件路径，如 applicationContext.xml。
     * 然后用这个配置文件创建了 AnnotationConfigWebApplicationContext 这一对象，我们叫 WAC，这就成了新的上下文。
     * 然后调用 servletContext.setAttribute() 方法，按照默认的属性值将 WAC 设置到 servletContext 里。
     * 这样，AnnotationConfigWebApplicationContext 和 servletContext 就能够互相引用了。
     */
    private void initWebApplicationContext(ServletContext servletContext) {
        String sContextLocation = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        //传入的就是配置文件的名字，由于AnnotationConfigWebApplicationContext继承了ClassPathXmlApplicationContext类
        //所以在new AnnotationConfigWebApplicationContext(sContextLocation)的时候，会调用ClassPathXmlApplicationContext中的构造函数
        //然后解析xml文件，注册bean的定义，再调用refresh方法，对所有bean进行实例化和初始化，并将applicationContext.xml中定义的bean存放在容器中
        WebApplicationContext wac = new AnnotationConfigWebApplicationContext(sContextLocation);
        wac.setServletContext(servletContext);
        this.webApplicationContext = wac;
        //tomcat创建的全局ServletContext
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.webApplicationContext);
    }
}
