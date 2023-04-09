package com.minis.context;

import java.util.EventObject;

/***
 * 观察者模式
 */
public class ApplicationEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    public ApplicationEvent(Object source) {
        super(source);
    }
}
