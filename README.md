# JVM｜原理分析和性能优化

------

**整理笔记、知识，知其然，知其所以然，将其中承载的价值传播分享出去**

------

## 1. ClassLoader类加载器

类加载器将生成的字节码class文件加载到JVM虚拟机内存中。

### 1) 类加载器分类

 - 启动类加载器，加载$JAVA_HOME/jre/lib下的文件
 - 扩展类加载器，加载$JAVA_HOME/jre/lib/ext下的文件
 - 应用类加载器，加载项目工程classpath下的文件
 - 自定义类加载器

### 2) 类加载器双亲委派机制

原理：当类加载器收到请求之后，首先会依次向上查找最顶层类加载器（启动类加载器），然后依次向下加载class文件，父加载器加载过的class文件，子加载器不会继续加载。

作用：避免开发者自定义的类名与JDK源码产生冲突，保证类在内存中的唯一性。

### 3) ClassLoader类loadClass方法源码解读

```java
protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
```

### 4) 自定义类加载器

### 5) SPI机制
![SPI机制加载自定义实现类][1]

---

## 2. 字节码文件分析

## 3. JVM内存结构

## 4. GC日志分析

## 5. 串行与并行收集器

## 6. CMS收集器

## 7. G1收集器

## 8. JVM参数调优

---


作者 @charles     
2021 年 08月 11日    


  [1]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/SPI%E6%9C%BA%E5%88%B6.png