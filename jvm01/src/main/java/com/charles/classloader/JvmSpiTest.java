package com.charles.classloader;

import com.charles.service.JvmSpiService;

import java.util.ServiceLoader;

/**
 * @author chales.tang
 * @title JvmSpiTest
 * @discription SPI机制测试
 */
public class JvmSpiTest {

    public static void main(String[] args) {

        // 设置当前线程的类加载器为扩展类加载器
//        Thread.currentThread().setContextClassLoader(JvmSpiTest.class.getClassLoader().getParent());

        // 通过SPI机制初始化JvmSpiService的实现类
        ServiceLoader<JvmSpiService> load = ServiceLoader.load(JvmSpiService.class);
        load.forEach( l -> System.out.println(l.getClass()));
    }
}
