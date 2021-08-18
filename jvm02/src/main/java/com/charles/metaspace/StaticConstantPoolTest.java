package com.charles.metaspace;

/**
 * @author chales.tang
 * @title StaticConstantPoolTest
 * @discription 静态常量池测试
 */
public class StaticConstantPoolTest {
    public static void main(String[] args) {
        String a1 = "a";
        String a2 = "a";
        // true 指向常量池中同一个地址
        System.out.println(a1 == a2);
    }
}

