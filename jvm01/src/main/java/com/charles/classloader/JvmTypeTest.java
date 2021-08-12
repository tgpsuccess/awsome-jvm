package com.charles.classloader;

import java.util.Arrays;
import java.util.List;

/**
 * @author chales.tang
 * @title JvmTest01
 * @discription
 */
public class JvmTest {

    public static void main(String[] args) {
        bootStrapClassLoader();
        System.out.println("--------------------------------------------------------");
        extClassLoader();
        System.out.println("--------------------------------------------------------");
        appClassLoader();
    }

    /**
     * @Discription 启动类加载器的职责
     */
    public static void bootStrapClassLoader() {
        String property = System.getProperty("sun.boot.class.path");
        List<String> list = Arrays.asList(property.split(":"));
        list.forEach(t -> System.out.println("启动类加载器扫包目录：" + t));
    }

    /**
     * @Discription 扩展类加载器的职责
     */
    public static void extClassLoader() {
        String property = System.getProperty("java.ext.dirs");
        List<String> list = Arrays.asList(property.split(":"));
        list.forEach(t -> System.out.println("扩展类加载器扫包目录：" + t));
    }

    /**
     * @Discription 应用类加载器的职责
     */
    public static void appClassLoader() {
        String property = System.getProperty("java.class.path");
        List<String> list = Arrays.asList(property.split(":"));
        list.forEach(t -> System.out.println("应用类加载器扫包目录：" + t));
    }
}
