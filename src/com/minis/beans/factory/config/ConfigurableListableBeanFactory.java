package com.minis.beans.factory.config;

import com.minis.beans.factory.ListableBeanFactory;

/***
 * 总接口，继承了其他接口的功能
 */
public interface ConfigurableListableBeanFactory extends
        ConfigurableBeanFactory,
        ListableBeanFactory,
        AutowireCapableBeanFactory {



}
