package com.minis.test;


import com.minis.web.RequestMapping;

public class HelloWorldBean {

    @RequestMapping("/test")
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
}