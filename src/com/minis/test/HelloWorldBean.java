package com.minis.test;


import com.minis.web.RequestMapping;

public class HelloWorldBean {

    @RequestMapping("/test")
    public String doTest() {
        return "hello world for doTest!";
    }

    public String doGet() {
        return "hello world for doGet!";
    }

    public String doPost() {
        return "hello world for doPost!";
    }
}