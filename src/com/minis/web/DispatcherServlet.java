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
    //分别记录 URL 对应的 MappingValue 对象、对应的类和对应的实例。
//    private Map<String, MappingValue> mappingValues;
//    private Map<String, Class<?>> mappingClz = new HashMap<>();

    //url名称与对象的映射关系
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

    /***
     * 0、扫描的文件需要web.xml文件中声明
     * 1、扫描servlet.xml文件，获取所有base-package的值
     * 2、根据base-package的值获取这些包下的所有类名（包名+类名的全路径），存入List controllerNames
     * 3、根据controllerNames全类名创建对应的类，存入Map<controllerName,Class<?>> controllerClasses
     * 4、根据对应的类创建对应的对象，存入Map<controllerName,Object> controllerObjs
     * 5、扫描所有controllerNames，是否存在被@RequestMapping注解的方法，然后拿到方法上注解的值（url）
     * 6、将url与方法名的映射存入Map<url,Method> mappingMethods，将url与对象映射存入Map<url, Object> mappingObjs
     * 7、在doGet方法中根据传入的url，拿到对应的对象，以及对应的方法（从第六步中的两个map中获取）
     * 8、result = method.invoke(obj);调用该方法，拿到返回值，传给view。
     */

    @Override
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
            } catch (Exception e) { }
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sPath = request.getServletPath();
        System.out.println("ServletPath:" + sPath); //ServletPath:/test  就是浏览器输入的url路径
        if (!this.urlMappingNames.contains(sPath)) {
            response.getWriter().append("no such url!");
            return;
        }
        Object obj = null;
        Object objResult = null;
        try {
            Method method = this.mappingMethods.get(sPath);
            obj = this.mappingObjs.get(sPath);
            objResult = method.invoke(obj);
        } catch (Exception e) { }

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
