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

    @Autowired
    private IAction action;

    @RequestMapping("/aop")
    public void doTestAop(HttpServletRequest request, HttpServletResponse response) {
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