package com.charles.classloader;

import com.charles.service.JvmSpiService;

/**
 * @author chales.tang
 * @title JvmHotDeploymentTest
 * @discription
 */
public class JvmHotDeploymentTest {

    private static long startTime;

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        MyClassLoader loader = new MyClassLoader("/Users/super/JvmSpiServiceImpl01.class");
        Class<?> oldClass = loader.findClass("com.charles.service.impl.JvmSpiServiceImpl01");
        System.out.println(oldClass.getClassLoader());

        JvmSpiService spiService = (JvmSpiService) oldClass.newInstance();
        System.out.println("读取class字节码文件成功：" + spiService.getName());

        startTime = loader.getLastModified();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long endTime = loader.getLastModified();
                if (startTime != endTime) {
                    System.out.println("class字节码文件发生了变化...");
                    try {
                        MyClassLoader newLoader = new MyClassLoader("/Users/super/JvmSpiServiceImpl01.class");
                        Class<?> newClass = newLoader.findClass("com.charles.service.impl.JvmSpiServiceImpl01");
                        JvmSpiService newSpiService = (JvmSpiService) newClass.newInstance();
                        System.out.println("读取class字节码文件成功：" + newSpiService.getName());
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    startTime = endTime;
                }
            }
        }).start();

    }
}
