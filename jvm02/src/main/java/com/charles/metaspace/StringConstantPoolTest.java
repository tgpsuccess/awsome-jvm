package com.charles.metaspace;

/**
 * @author chales.tang
 * @title StringConstantPoolTest
 * @discription 字符串常量池测试
 */
public class StringConstantPoolTest {

    public static void main(String[] args) {
        // 1. 常量池的中的字符串仅是符号，第一次使用的时候变为对象
        String s1 = "a";
        // 2. 利用串池的机制，来避免重复创建字符串对象
        String s2 = "b";
        // 3. 字符串常量拼接的原理是编译器优化
        String s3 = "a" + "b";
        // 4. 字符串变量拼接原理采用StringBuilder（1.8）
        String s4 = s1 + s2;
        String s5 = "ab";
        // 5. 使用intern方法，主动将堆内存的字符串对象放入字符串常量池中
        String s6 = s4.intern();
        System.out.println(s3 == s4); // false
        System.out.println(s3 == s5); // true
        System.out.println(s3 == s6); // true
    }
}
