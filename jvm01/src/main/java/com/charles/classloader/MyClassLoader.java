package com.charles.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author chales.tang
 * @title MyClassLoader
 * @discription 自定义ClassLoader
 */
public class MyClassLoader extends ClassLoader {

    private String fileName;

    private File file;

    public MyClassLoader(String fileName) {
        this.fileName = fileName;
    }

    public long getLastModified() {
        return this.file.lastModified();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        ClassLoader system = getSystemClassLoader();
        if (!name.equals("com.charles.service.impl.JvmSpiServiceImpl01")) {
            clazz = system.loadClass(name);
        }
        if (clazz != null) {
            return clazz;
        }
        clazz = findClass(name);
        return clazz;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            file = new File(fileName);
            byte[] fileBytes = getClassFileBytes(file);
            return defineClass(name, fileBytes, 0, fileBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从文件中读取去class文件
     *
     * @throws Exception
     */
    private byte[] getClassFileBytes(File file) throws Exception {
        //采用NIO读取
        FileInputStream fis = new FileInputStream(file);
        FileChannel fileC = fis.getChannel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel outC = Channels.newChannel(baos);
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        while (true) {
            int i = fileC.read(buffer);
            if (i == 0 || i == -1) {
                break;
            }
            buffer.flip();
            outC.write(buffer);
            buffer.clear();
        }
        fis.close();
        return baos.toByteArray();
    }

}
