package com.minis.beans;

//自定义异常
public class BeansException extends Exception{

    public BeansException() {
        super();
    }
    public BeansException(String msg) {
        super(msg);
    }
}
