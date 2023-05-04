package com.minis.test.controller;


import com.minis.test.entity.User;
import com.minis.web.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;

import java.util.Date;

public class HelloWorldBean {

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