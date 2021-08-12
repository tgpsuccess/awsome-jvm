package com.charles.classloader;

/**
 * @author chales.tang
 * @title JvmLoadClassTest
 * @discription ClassLoader类loadClass方法解读
 */
public class JvmLoadClassTest {

    public static void main(String[] args) throws ClassNotFoundException {

        // 加载class
        ClassLoader.getSystemClassLoader().loadClass(JvmEntity.class.getName());

        // new之前先加载并初始化该类
        JvmEntity jvmEntity = new JvmEntity();

    }

}
