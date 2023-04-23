package com.minis.web;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DispatcherServlet extends HttpServlet {
    //分别记录 URL 对应的 MappingValue 对象、对应的类和对应的方法。
    private Map<String, MappingValue> mappingValues;
    private Map<String, Class<?>> mappingClz = new HashMap<>();
    private Map<String, Object> mappingObjs = new HashMap<>();

    //需要扫描的package列表
    private List<String> packageNames = new ArrayList<>();
    //存储controller的名称与对象的映射
    private Map<String,Object> controllerObjs = new HashMap<>();
    //controller名称
    private List<String> controllerNames = new ArrayList<>();
    //controller的名称与类的映射
    private Map<String,Class<?>> controllerClasses = new HashMap<>();
    //保存自定义的@RequestMapping url名称列表
    private List<String> urlMappingNames = new ArrayList<>();
    //url与方法的映射
    private Map<String,Method> mappingMethods = new HashMap<>();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        //获取到minisMVC-servlet.xml文件路径
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        URL xmlPath = null;

        try {
            xmlPath = this.getServletContext().getResource(contextConfigLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);

        Refresh();
    }

    //对所有的mappingValues中注册的类进行实例化，默认构造函数
    protected void Refresh() {
        initController();
        initMapping();
    }


    protected void initController() {
        //扫描包，获取所有类名
        this.controllerNames = scanPackages(this.packageNames);
        for (String controllerName : this.controllerNames) {
            Object obj = null;
            Class<?> clz = null;
            try {
                clz = Class.forName(controllerName); //加载类
                this.controllerClasses.put(controllerName, clz);
            } catch (Exception e) {
            }
            try {
                obj = clz.newInstance(); //实例化bean
                this.controllerObjs.put(controllerName, obj);
            } catch (Exception e) {
            }
        }
    }


    private List<String> scanPackages(List<String> packages) {
        List<String> tempControllerNames = new ArrayList<>();
        for (String packageName : packages) {
            tempControllerNames.addAll(scanPackage(packageName));
        }
        return tempControllerNames;
    }

    //得到包下的所有类名
    private List<String> scanPackage(String packageName) {
        List<String> tempControllerNames = new ArrayList<>();
        URI uri = null;
        //将以.分隔的包名换成以/分隔的uri，目的是为了构造出对应的文件路径
        try {
            uri = this.getClass().getResource("/" + packageName.replaceAll("\\.", "/")).toURI();
        } catch (Exception e) { }

        File dir = new File(uri);
        //处理对应的文件目录
        for (File file : dir.listFiles()) { //目录下的文件或者子目录
            if(file.isDirectory()){ //对子目录递归扫描
                scanPackage(packageName+"."+file.getName());
            }else{ //类文件名字与类名相同
                String controllerName = packageName +"." + file.getName().replace(".class", "");
                tempControllerNames.add(controllerName);
            }
        }
        //返回的类名包含了完整的包名.类名，即可以通过反射直接构造该类
        return tempControllerNames;
    }

    protected void initMapping() {
        for (String controllerName : this.controllerNames) {
            Class<?> clazz = this.controllerClasses.get(controllerName);
            Object obj = this.controllerObjs.get(controllerName);
            Method[] methods = clazz.getDeclaredMethods();
            if (methods != null) {
                for (Method method : methods) {
                    //检查所有的方法
                    boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                    if (isRequestMapping) { //有RequestMapping注解
                        String methodName = method.getName();
                        //建立方法名和URL的映射
                        String urlMapping = method.getAnnotation(RequestMapping.class).value();
                        this.urlMappingNames.add(urlMapping);
                        this.mappingObjs.put(urlMapping, obj);
                        this.mappingMethods.put(urlMapping, method);
                    }
                }
            }
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sPath = request.getServletPath();
        if (!this.urlMappingNames.contains(sPath)) {
            return;
        }
        Object obj = null;
        Object objResult = null;
        try {
            Method method = this.mappingMethods.get(sPath);
            obj = this.mappingObjs.get(sPath);
            objResult = method.invoke(obj);
        } catch (Exception e) {
        }
        response.getWriter().append(objResult.toString());
    }

//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        //获取请求path，也就是servlet.xml文件中的id
//        String path = request.getServletPath();
//        if (mappingValues.get(path) == null) {
//            return;
//        }
//        //获取bean类的定义
//        Class<?> clz = mappingClz.get(path);
//        //获取bean的实例
//        Object obj = mappingObjs.get(path);
//        //获取调用的方法名
//        String methodName = this.mappingValues.get(path).getMethod();
//        Object objResult = null;
//
//        try {
//            Method method = clz.getMethod(methodName);
//            objResult = method.invoke(obj);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//
//        //将方法返回值写入response
//        response.getWriter().append(objResult.toString());
//    }
}
