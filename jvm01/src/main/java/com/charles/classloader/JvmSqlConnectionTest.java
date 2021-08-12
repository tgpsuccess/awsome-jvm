package com.charles.classloader;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ServiceLoader;

/**
 * @author chales.tang
 * @title JvmSqlConnectionTest
 * @discription Mysql驱动包下Driver驱动类加载测试
 */
public class JvmSqlConnectionTest {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        // 设置当前线程的类加载器为扩展类加载器，会导致线程加载不到mysql驱动包下java.sql.Driver的实现类
//        Thread.currentThread().setContextClassLoader(JvmSqlConnectionTest.class.getClassLoader().getParent());
        Connection root = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/mysql?characterEncoding=UTF-8",
                "root", "root");

        // 通过反射机制加载mysql驱动包下java.sql.Driver的实现类com.mysql.jdbc.Driver
        Class<?> aClass = Class.forName("com.mysql.jdbc.Driver");
        System.out.println(aClass.getClassLoader());
        System.out.println(Thread.currentThread().getContextClassLoader());

        // 通过SPI机制懒加载mysql驱动包下java.sql.Driver的实现类
        ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
        loadedDrivers.forEach(System.out::println);

        // java.sql.Driver类在rt.jar包，由启动类加载器进行加载，所以它的加载器为null
        System.out.println(ClassLoader.getSystemClassLoader().loadClass("java.sql.Driver").getClassLoader());

    }
}
