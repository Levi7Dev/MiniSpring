package com.minis.test.controller;


import com.minis.beans.factory.annotation.Autowired;
import com.minis.test.entity.User;
import com.minis.test.service.IAction;
import com.minis.web.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class HelloWorldBean {

    /***
     * action在xml文件中对应的类为FactoryBean，
     * bean实例化时，在getBean方法中进行了判断，如果为FactoryBean的实例，则调用方法，获取该类下的target成员变量
     * 实例化后已经是被代理的对象，当调用内部的方法时会自动调用动态代理的invoke方法
     */
    @Autowired
    private IAction action;

    @RequestMapping("/aop")
    public void doTestAop(HttpServletRequest request, HttpServletResponse response) {
        //action已经是内部被代理的对象，当调用具体方法是会自动调用JdkDynamicAopProxy中的invoke方法，然后在内部调用具体的方法
        action.doAction();
        String str = "test aop, hello world!";
        try {
            response.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/test")
    @ResponseBody
    public String doTest() {
        return "hello world for doTest!";
    }

    @RequestMapping("/get")
    public String doGet() {
        return "hello world for doGet!";
    }

    @RequestMapping("/post")
    public String doPost() {
        return "hello world for doPost!";
    }


    @RequestMapping("/test7")
    @ResponseBody
    public User doTest7(User user) {
        user.setName(user.getName() + "---");
        user.setBirthday(new Date());
        return user;
    }
}