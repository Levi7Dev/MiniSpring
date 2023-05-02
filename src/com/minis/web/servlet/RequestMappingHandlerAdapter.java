package com.minis.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minis.beans.BeansException;
import com.minis.web.WebApplicationContext;
import com.minis.web.WebBindingInitializer;
import com.minis.web.WebDataBinder;
import com.minis.web.WebDataBinderFactory;

/***
 * 获取向前端返回的数据
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter {
    WebApplicationContext wac;
    WebBindingInitializer webBindingInitializer;

    public RequestMappingHandlerAdapter(WebApplicationContext wac) throws BeansException {
        this.wac = wac;
        this.webBindingInitializer = (WebBindingInitializer) this.wac.getBean("webBindingInitializer");
    }

    //真正执行需要调用该方法
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
//        //获取到方法和对象，再调用方法得到结果，最后返回数据到前端
//        Method method = handler.getMethod();
//        Object obj = handler.getBean();
//        Object objResult = null;
//        try {
//            //调用方法，返回结果
//            objResult = method.invoke(obj);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            response.getWriter().append(objResult.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            invokeHandlerMethod(request, response, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //处理参数并绑定
    protected void invokeHandlerMethod(HttpServletRequest request,
                                       HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        WebDataBinderFactory binderFactory = new WebDataBinderFactory();
        Parameter[] methodParameters = handlerMethod.getMethod().getParameters();
        Object[] methodParamObjs = new Object[methodParameters.length];
        int i = 0;
        //对调用方法里的每一个参数，处理绑定
        for (Parameter methodParameter : methodParameters) {
            Object methodParamObj = methodParameter.getType().newInstance();
            //给这个参数创建WebDataBinder
            WebDataBinder wdb = binderFactory.createBinder(request, methodParamObj, methodParameter.getName());
            wdb.bind(request);
            methodParamObjs[i] = methodParamObj;
            i++;
        }
        Method invocableMethod = handlerMethod.getMethod();
        Object returnObj = invocableMethod.invoke(handlerMethod.getBean(), methodParamObjs);
        response.getWriter().append(returnObj.toString());
    }
}
