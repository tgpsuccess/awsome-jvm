package com.charles.heap;

import java.util.ArrayList;

/**
 * @author chales.tang
 * @title HeapOverflowTest
 * @discription 堆内存溢出测试
 */
public class HeapOverflowTest {

    /**
     * 设置参数：-Xmx8m
     * Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
     */
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        while (true){
            strings.add("test");
        }
    }
}
