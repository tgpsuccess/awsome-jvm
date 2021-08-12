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

**默认情况下，当前线程关联的是应用类加载器。**
![默认情况下，当前线程关联的是应用类加载器][1]
 
### 2) 类加载器双亲委派机制

原理：当类加载器收到请求之后，首先会依次向上查找最顶层类加载器（启动类加载器），然后依次向下加载class文件，父加载器加载过的class文件，子加载器不会继续加载。

作用：避免开发者自定义的类名与JDK源码产生冲突，保证类在内存中的唯一性。

### 3) ClassLoader类loadClass方法源码解读

> 双亲委派机制执行类加载的过程

当一个类加载器收到了类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器去完成，因此所有的加载请求最终都传送到顶层的启动类加载器中，只有当父加载器反馈自己无法完成这个加载请求（找不到所需的类）时，子加载器才会尝试自己去加载。

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

### 4) SPI机制

Java SPI全称Service Provider Interface
是Java提供的一套用来被第三方实现或者扩展的API，它可以用来启用框架扩展和替换组件。实际上是“基于接口的编程＋策略模式＋配置文件”组合实现的动态加载机制.

> SPI机制具体实现方式

 - 首先需要再resources目录下：创建文件夹 META-INF.services
 - 定义接口文件的名称：
\src\main\resources\META-INF\services\com.charles.service.JvmSpiService

![SPI机制加载自定义实现类][2]

> SPI机制如何绕过ClassLoader类loadClass方法

查找当前线程类加载器目录下是否由SPI机制对应的配置文件，如果没有，则初始化该类失败，抛出异常。
![Mysql驱动实现类初始化失败][3]

### 5) 自定义类加载器

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


  [1]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/%E5%BD%93%E5%89%8D%E7%BA%BF%E7%A8%8B%E9%BB%98%E8%AE%A4%E4%BD%BF%E7%94%A8%E5%BA%94%E7%94%A8%E7%B1%BB%E5%8A%A0%E8%BD%BD%E5%99%A8.png
  [2]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/SPI%E6%9C%BA%E5%88%B6.png
  [3]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/Mysql%E9%A9%B1%E5%8A%A8%E5%AE%9E%E7%8E%B0%E7%B1%BB%E5%88%9D%E5%A7%8B%E5%8C%96%E5%A4%B1%E8%B4%A5.png