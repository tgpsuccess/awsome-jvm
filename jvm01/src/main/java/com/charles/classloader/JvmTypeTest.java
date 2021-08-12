package com.charles.classloader;

import java.util.Arrays;
import java.util.List;

/**
 * @author chales.tang
 * @title JvmTest01
 * @discription 类加载器加载目录测试
 */
public class JvmTypeTest {

    public static void main(String[] args) {
        bootStrapClassLoader();
        System.out.println("--------------------------------------------------------");
        extClassLoader();
        System.out.println("--------------------------------------------------------");
        appClassLoader();
    }

    /**
     * @Discription 启动类加载器的职责，加载 $JAVA_HOME/jre/lib下的文件
     */
    public static void bootStrapClassLoader() {
        String property = System.getProperty("sun.boot.class.path");
        List<String> list = Arrays.asList(property.split(":"));
        list.forEach(t -> System.out.println("启动类加载器扫包目录：" + t));
    }

    /**
     * @Discription 扩展类加载器的职责，加载 $JAVA_HOME/jre/lib/ext下的文件
     */
    public static void extClassLoader() {
        String property = System.getProperty("java.ext.dirs");
        List<String> list = Arrays.asList(property.split(":"));
        list.forEach(t -> System.out.println("扩展类加载器扫包目录：" + t));
    }

    /**
     * @Discription 应用类加载器的职责，加载工程classpath目录下的class文件及jar包
     */
    public static void appClassLoader() {
        String property = System.getProperty("java.class.path");
        List<String> list = Arrays.asList(property.split(":"));
        list.forEach(t -> System.out.println("应用类加载器扫包目录：" + t));
    }
}
