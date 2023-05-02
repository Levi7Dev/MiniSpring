package com.minis.web;

import javax.servlet.http.HttpServletRequest;

/***
 * 更方便、灵活地操作 WebDataBinder
 */
public class WebDataBinderFactory {

    public WebDataBinder createBinder(HttpServletRequest request, Object target, String objectName) {
        WebDataBinder wbd = new WebDataBinder(target, objectName);
        initBinder(wbd, request);
        return wbd;
    }

    protected void initBinder(WebDataBinder dataBinder, HttpServletRequest request) {
    }
}
