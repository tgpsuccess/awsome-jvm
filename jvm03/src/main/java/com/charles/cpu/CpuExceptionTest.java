package com.charles.cpu;

/**
 * @author chales.tang
 * @title CpuExceptionTest
 * @discription CPU飙升问题测试
 */
public class CpuExceptionTest {

    public static void main(String[] args) {
        new Thread(() -> {
            while (true){
                System.out.println("test");
            }
        }, "cpu-test").start();
    }
}
