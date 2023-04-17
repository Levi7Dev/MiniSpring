package com.minis.context;

import java.util.EventObject;

/***
 * 事件的发布与监听
 */
public class ApplicationEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    protected String msg = null;

    public ApplicationEvent(Object source) {
        super(source);
        this.msg = source.toString();
    }
}
