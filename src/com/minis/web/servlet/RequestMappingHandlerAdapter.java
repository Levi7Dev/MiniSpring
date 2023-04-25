package com.minis.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minis.web.WebApplicationContext;

/***
 * 获取向前端返回的数据
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter {
    WebApplicationContext wac;

    public RequestMappingHandlerAdapter(WebApplicationContext wac) {
        this.wac = wac;
    }

    //真正执行需要调用该方法
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        //获取到方法和对象，再调用方法得到结果，最后返回数据到前端
        Method method = handler.getMethod();
        Object obj = handler.getBean();
        Object objResult = null;
        try {
            //调用方法，返回结果
            objResult = method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            response.getWriter().append(objResult.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
