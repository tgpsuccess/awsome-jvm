package com.charles.stack;

/**
 * @author chales.tang
 * @title StackFrameTest
 * @discription 栈帧数据结构测试
 */
public class StackFrameTest {

    /**
     * 一个方法对应一个栈帧空间
     */
    public String method01(){
        return method02();
    }

    public String method02(){
        return "Test";
    }

    public static void main(String[] args) {
        StackFrameTest test = new StackFrameTest();
        System.out.println(test.method01());
    }
}
