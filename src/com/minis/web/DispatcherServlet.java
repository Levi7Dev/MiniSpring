package com.minis.web;

import com.minis.beans.BeansException;
import com.minis.web.servlet.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";
    public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";
    public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";

    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;

    private WebApplicationContext webApplicationContext;
    //新增 parentApplicationContext 的目的是把 Listener 启动的上下文和 DispatcherServlet 启动的上下文两者区分开来。
    //按照时序关系，Listener 启动在前，对应的上下文我们把它叫作 parentApplicationContext。
    private WebApplicationContext parentApplicationContext;
    private String sContextConfigLocation;

//    //url名称与对象的映射关系
//    private Map<String, Object> mappingObjs = new HashMap<>();
//    //需要扫描的package列表
//    private List<String> packageNames = new ArrayList<>();
//    //存储controller的名称与对象的映射
//    private Map<String, Object> controllerObjs = new HashMap<>();
//    //controller名称
//    private List<String> controllerNames = new ArrayList<>();
//    //controller的名称与类的映射
//    private Map<String, Class<?>> controllerClasses = new HashMap<>();
//    //保存自定义的@RequestMapping url名称列表
//    private List<String> urlMappingNames = new ArrayList<>();
//    //url与方法的映射
//    private Map<String, Method> mappingMethods = new HashMap<>();

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

        /**
         * 首先在 Servlet 初始化的时候，从 sevletContext 里获取属性，
         * 拿到 Listener 启动的时候注册好的 WebApplicationContext，
         * 然后拿到 Servlet 配置参数 contextConfigLocation，
         * 这个参数代表的是配置文件路径，这个时候是我们的 MVC 用到的配置文件，如 minisMVC-servlet.xml，
         * 之后再扫描路径下的包，调用 refresh() 方法加载 Bean。
         * 这样，DispatcherServlet 也就初始化完毕了。
         */

        /***
         * 初始化的时候先从 ServletContext 里拿属性 WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE，
         * 得到的是前一步 Listener 存放在这里的那个 parentApplicationContext。
         * 然后通过 contextConfigLocation 配置文件，创建一个新的 WebApplicationContext。（本函数倒数第二行代码）
         */
        this.parentApplicationContext =
                (WebApplicationContext) this.getServletContext()
                        .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        //获取到minisMVC-servlet.xml文件路径
        sContextConfigLocation = config.getInitParameter("contextConfigLocation");
//        URL xmlPath = null;
//        try {
//            xmlPath = this.getServletContext().getResource(sContextConfigLocation);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

        //this.packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);

        //新new的上下文，属于DispatcherServlet 启动的上下文，不来自listener
        //在构造函数中，会拿到来自listener中的上下文，并且会扫描minisMVC-servlet.xml中的包，所以不需要上面那些代码区单独扫描包拿到controller
        //这步完成已经拿到了包下的所有controller的全类名
        this.webApplicationContext =
                new AnnotationConfigWebApplicationContext(sContextConfigLocation, this.parentApplicationContext);

        try {
            Refresh();
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }

    protected void Refresh() throws BeansException {
        //initController();
        //initMapping();
        //获取方法和对应的对象
        initHandlerMappings(this.webApplicationContext);
        //获取需要向前端返回的数据
        initHandlerAdapters(this.webApplicationContext);
    }

    protected void initHandlerMappings(WebApplicationContext wac) {
        //this.handlerMapping = new RequestMappingHandlerMapping(webApplicationContext);
        try {
            this.handlerMapping = (HandlerMapping) wac.getBean(HANDLER_MAPPING_BEAN_NAME);
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }

    protected void initHandlerAdapters(WebApplicationContext wac) {
        //this.handlerAdapter = new RequestMappingHandlerAdapter(webApplicationContext);
        try {
            this.handlerAdapter = (HandlerAdapter) wac.getBean(HANDLER_ADAPTER_BEAN_NAME);
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.webApplicationContext);
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally { }
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerMethod handlerMethod = null;
        //传入request，在getHandler方法中获取传入的url，再根据url拿到对应的handlerMethod（封装了方法和对象）
        handlerMethod = this.handlerMapping.getHandler(processedRequest);
        if (handlerMethod == null) {
            response.getWriter().append("no such url!");
            return;
        }
        //在前面已经实例化
        HandlerAdapter ha = this.handlerAdapter;
        //调用handle方法才会真正执行向前端返回数据的操作
        ha.handle(processedRequest, response, handlerMethod);
    }


//    protected void initController() {
//        //扫描包，获取所有类名
//        this.controllerNames = scanPackages(this.packageNames);
//        for (String controllerName : this.controllerNames) {
//            Object obj = null;
//            Class<?> clz = null;
//            try {
//                clz = Class.forName(controllerName); //加载类
//                this.controllerClasses.put(controllerName, clz);
//            } catch (Exception e) {
//            }
//            try {
//                obj = clz.newInstance(); //实例化bean
//                this.controllerObjs.put(controllerName, obj);
//            } catch (Exception e) {
//            }
//        }
//    }
//
//
//    private List<String> scanPackages(List<String> packages) {
//        List<String> tempControllerNames = new ArrayList<>();
//        for (String packageName : packages) {
//            tempControllerNames.addAll(scanPackage(packageName));
//        }
//        return tempControllerNames;
//    }
//
//    //得到包下的所有类名
//    private List<String> scanPackage(String packageName) {
//        List<String> tempControllerNames = new ArrayList<>();
//        URI uri = null;
//        //将以.分隔的包名换成以/分隔的uri，目的是为了构造出对应的文件路径
//        try {
//            uri = this.getClass().getResource("/" + packageName.replaceAll("\\.", "/")).toURI();
//        } catch (Exception e) {
//        }
//
//        File dir = new File(uri);
//        //处理对应的文件目录
//        for (File file : dir.listFiles()) { //目录下的文件或者子目录
//            if (file.isDirectory()) { //对子目录递归扫描
//                scanPackage(packageName + "." + file.getName());
//            } else { //类文件名字与类名相同
//                String controllerName = packageName + "." + file.getName().replace(".class", "");
//                tempControllerNames.add(controllerName);
//            }
//        }
//        //返回的类名包含了完整的包名.类名，即可以通过反射直接构造该类
//        return tempControllerNames;
//    }
//
//    protected void initMapping() {
//        //会扫描报包下的所有controllerNames，比较耗时，所以尽量精确化地声明需要扫描的类
//        for (String controllerName : this.controllerNames) {
//            Class<?> clazz = this.controllerClasses.get(controllerName);
//            Object obj = this.controllerObjs.get(controllerName);
//            Method[] methods = clazz.getDeclaredMethods();
//            if (methods != null) {
//                for (Method method : methods) {
//                    //检查所有的方法
//                    boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
//                    if (isRequestMapping) { //有RequestMapping注解
//                        String methodName = method.getName();
//                        //建立方法名和URL的映射，拿到方法上注解传入的值
//                        String urlMapping = method.getAnnotation(RequestMapping.class).value();
//                        this.urlMappingNames.add(urlMapping);
//                        this.mappingObjs.put(urlMapping, obj);
//                        this.mappingMethods.put(urlMapping, method);
//                    }
//                }
//            }
//        }
//    }

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String sPath = request.getServletPath();
//        System.out.println("ServletPath:" + sPath); //ServletPath:/test  就是浏览器输入的url路径
//        if (!this.urlMappingNames.contains(sPath)) {
//            response.getWriter().append("no such url!");
//            return;
//        }
//        Object obj = null;
//        Object objResult = null;
//        try {
//            Method method = this.mappingMethods.get(sPath);
//            obj = this.mappingObjs.get(sPath);
//            objResult = method.invoke(obj);
//        } catch (Exception e) {
//        }
//
//        response.getWriter().append(objResult.toString());
//    }
}
