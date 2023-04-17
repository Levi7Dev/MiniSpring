package com.minis.core.env;

/***
 * 提供对环境属性的访问和处理能力，包括系统属性、环境变量、配置文件等。
 * Environment 接口在 Spring 应用上下文（如：ApplicationContext）中扮演了一个重要的角色，它是 Spring 环境抽象的核心接口。
 */
public interface Environment extends PropertyResolver{
    //获取当前激活的配置文件列表（如：Spring Boot 的 application-{profile}.properties 文件）。
    String[] getActiveProfiles();
    //获取默认的配置文件列表。
    String[] getDefaultProfiles();
    //判断当前环境是否接受给定的配置文件列表。
    boolean acceptsProfiles(String... profiles);
}
