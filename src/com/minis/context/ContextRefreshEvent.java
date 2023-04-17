package com.minis.context;

public class ContextRefreshEvent extends ApplicationEvent{
    private static final long serialVersionUID = 1L;

    public ContextRefreshEvent(Object source) {
        super(source);
    }

    public String toString() {
        //父类ApplicationEvent中的消息
        return this.msg;
    }
}
