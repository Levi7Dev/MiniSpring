package com.minis.test.service;

import com.minis.test.service.AServiceImpl;

public class BaseBaseService {
    private AServiceImpl as;

    public BaseBaseService() {
    }

    public void sayHello() {
        System.out.println("Base Base Service says hello");
    }

    public void init() {
        System.out.println("Base Base Service init method.");
    }

    public AServiceImpl getAs() {
        return as;
    }

    public void setAs(AServiceImpl as) {
        this.as = as;
    }
}
