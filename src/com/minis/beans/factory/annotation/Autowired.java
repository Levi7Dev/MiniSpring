package com.minis.beans.factory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//注解的作用目标
@Target(ElementType.FIELD)
//注解保留多久
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

}
