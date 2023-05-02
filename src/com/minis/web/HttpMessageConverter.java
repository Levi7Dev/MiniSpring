package com.minis.web;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/***
 * 将 controller 返回给前端的字符流数据进行格式转换
 */
public interface HttpMessageConverter {
    void write(Object obj, HttpServletResponse response) throws IOException;
}