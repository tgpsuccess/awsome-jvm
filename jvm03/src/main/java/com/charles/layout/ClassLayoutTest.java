package com.charles.layout;

import org.openjdk.jol.info.ClassLayout;

/**
 * @author chales.tang
 * @title ClassLayoutTest
 * @discription 类布局分析测试
 */
public class ClassLayoutTest {

    private int id;

    public static void main(String[] args) {
        ClassLayoutTest test = new ClassLayoutTest();
        // 生成类布局的可打印字符串表
        System.out.println(ClassLayout.parseInstance(test).toPrintable());
    }
}
