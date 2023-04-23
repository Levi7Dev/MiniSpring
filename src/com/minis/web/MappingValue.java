package com.minis.web;

public class MappingValue {
    //分别与 minisMVC-servlet.xml 中标签的属性 id（id传入的就是请求路径）、class 与 value（填入的是方法名字）对应。
    String uri;
    String clz;
    String method;

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getClz() {
        return clz;
    }
    public void setClz(String clz) {
        this.clz = clz;
    }

    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    public MappingValue(String uri, String clz, String method) {
        this.uri = uri;
        this.clz = clz;
        this.method = method;
    }
}