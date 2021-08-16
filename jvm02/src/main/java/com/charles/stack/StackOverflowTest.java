package com.charles.stack;

/**
 * @author chales.tang
 * @title StackOverflowTest
 * @discription 栈内存溢出测试
 */
public class StackOverflowTest {

    private static int count;

    public static void main(String[] args) {
        method();
    }

    /**
     * -Xss256k
     * Exception in thread "main" java.lang.StackOverflowError
     * 递归方法调用
     */
    public static void method() {
        System.out.println("count : " + count++);
        method();
    }
}
