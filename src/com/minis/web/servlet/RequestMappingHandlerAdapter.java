package com.minis.web.servlet;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minis.beans.BeansException;
import com.minis.context.ApplicationContext;
import com.minis.web.*;
import com.minis.web.bind.annotation.ResponseBody;

/***
 * 获取向前端返回的数据
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter{
    WebApplicationContext wac;
    WebBindingInitializer webBindingInitializer = null;
    HttpMessageConverter messageConverter = null;

    public RequestMappingHandlerAdapter() {
    }

    public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
        this.webBindingInitializer = webBindingInitializer;
    }

    public void setMessageConverter(HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public RequestMappingHandlerAdapter(WebApplicationContext wac) throws BeansException {
        this.wac = wac;
        this.webBindingInitializer = (WebBindingInitializer) this.wac.getBean("webBindingInitializer");
    }

    //真正执行需要调用该方法
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        ModelAndView mv = null;
        try {
            mv = invokeHandlerMethod(request, response, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mv;
    }

    //处理参数并绑定
    protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
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
        Class<?> returnType = invocableMethod.getReturnType();

        ModelAndView mav = null;
        if (invocableMethod.isAnnotationPresent(ResponseBody.class)) { //ResponseBody
            this.messageConverter.write(returnObj, response);
        } else {
            if (returnObj instanceof ModelAndView) {
                mav = (ModelAndView) returnObj;
            } else if (returnObj instanceof String) {
                String sTarget = (String)returnObj;
                mav = new ModelAndView();
                mav.setViewName(sTarget);
            }
        }
        return mav;
        //response.getWriter().append(returnObj.toString());
    }
}
