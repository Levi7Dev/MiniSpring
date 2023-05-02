package com.minis.test.service;


import com.minis.beans.factory.annotation.Autowired;
import com.minis.test.service.BaseBaseService;

public class BaseService {

    //会根据bbs这个属性名字匹配
    @Autowired
    private BaseBaseService bbs;

    public BaseService() {
    }

    public void sayHello() {
        System.out.println("Base Service says hello");
    }

    public BaseBaseService getBbs() {
        return bbs;
    }

    public void setBbs(BaseBaseService bbs) {
        this.bbs = bbs;
    }

    public void init() {
        System.out.print("Base Service init method.");
    }
}