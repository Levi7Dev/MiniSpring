package com.minis.test;

public class AServiceImpl implements AService {
    private String property1;

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    @Override
    public void sayHello() {
        System.out.println("a service 1 say hello");
    }
}
