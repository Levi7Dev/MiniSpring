package com.minis.web.servlet;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface View {
    //这个 render() 方法的思路是获取 HTTP 请求的 request 和 response，以及中间产生的业务数据 Model，最后写到 response 里面。
    void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

    default String getContentType() {
        return null;
    }

    void setContentType(String contentType);

    void setUrl(String url);

    String getUrl();

    void setRequestContextAttribute(String requestContextAttribute);

    String getRequestContextAttribute();
}
