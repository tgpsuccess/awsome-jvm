# JVM｜原理分析和性能优化

------

**整理笔记、知识，知其然，知其所以然，将其中承载的价值传播分享出去**

[1.ClassLoader类加载器][1]
[1.1 类加载器分类][2]
[1.2 类加载器双亲委派机制][3]

## 1. ClassLoader类加载器

类加载器将生成的字节码class文件加载到JVM虚拟机内存中。

### 1.1 类加载器分类

 - 启动类加载器，加载$JAVA_HOME/jre/lib下的文件
 - 扩展类加载器，加载$JAVA_HOME/jre/lib/ext下的文件
 - 应用类加载器，加载项目工程classpath下的文件
 - 自定义类加载器

> 默认情况下，当前线程关联的是应用类加载器

![默认情况下，当前线程关联的是应用类加载器][4]
 
### 1.2 类加载器双亲委派机制

原理：当类加载器收到请求之后，首先会依次向上查找最顶层类加载器（启动类加载器），然后依次向下加载class文件，父加载器加载过的class文件，子加载器不会继续加载。

作用：避免开发者自定义的类名与JDK源码产生冲突，保证类在内存中的唯一性。

### 1.3 ClassLoader类loadClass方法源码解读

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

### 1.4 SPI机制

Java SPI全称Service Provider Interface
是Java提供的一套用来被第三方实现或者扩展的API，它可以用来启用框架扩展和替换组件。实际上是“基于接口的编程＋策略模式＋配置文件”组合实现的动态加载机制.

> SPI机制具体实现方式

 - 首先需要再resources目录下：创建文件夹 META-INF.services
 - 定义接口文件的名称：
\src\main\resources\META-INF\services\com.charles.service.JvmSpiService

![SPI机制加载自定义实现类][5]

> SPI机制如何绕过ClassLoader类loadClass方法

查找当前线程类加载器目录下是否由SPI机制对应的配置文件，如果没有，则初始化该类失败，抛出异常。
![Mysql驱动实现类初始化失败][6]

### 1.5 自定义类加载器

重写ClassLoader的findClass方法。

---

## 2. 字节码文件分析

------

## 3. JVM内存结构

### 3.1 堆（Heap）

多线程共享的内存区域，在JVM启动时创建，是JVM中最大的内存区域，用于存储应用的**`对象实例和数组`**，也是GC主要的回收区，一个 JVM 实例只存在一个堆内存。

> 堆内存溢出

java.lang.OutOfMemoryError: Java heap space
申请内存不足，导致堆内存溢出。
![堆内存溢出][7]

> 堆内存泄漏

java.lang.OutOfMemoryError: GC overhead limit exceeded
概念：被占用的内存，经过多次长时间的GC操作都无法回收，导致可用内存越来越少。
![堆内存泄漏][8]

### 3.2 栈（Stack）

Java栈，又称线程栈，是线程私有的，在线程创建时被创建，用于存储**`局部变量、JVM栈帧、栈操作`**。它和线程的生命期周期保持一致，线程结束栈内存即被释放，所以对于栈来说不存在垃圾回收问题。

> 栈帧

栈帧就是每个方法需要的运行时内存空间。一个方法对应一个栈帧内存空间，每个方法都有独立的栈帧内存空间。栈帧采用先进后出、后进先出的方式进行内存空间的销毁。

![栈帧数据结构测试][9]

> 栈内存溢出

栈空间产生过多的栈帧内存空间一直得不到释放，导致内存溢出。例如，递归方法的调用。

![栈内存溢出][10]

### 3.3 本地方法栈（Native）

本地方法栈为JVM使用到的Native方法服务。具体做法是在本地方法栈中登记native方法，在执行引擎执行时加载Native Liberies。也就是 Java调用C语言代码JNI技术。

### 3.4 方法区（元空间）

多线程共享，存在线程安全问题。存放**`类的信息、常量、静态变量和运行时常量`**。

### 3.5 常量池

概念：虚拟机根据该常量池表找到执行的类名、方法名、参数类型、字面量。

#### 3.5.1 二进制字节码

Class字节码，包括类基本信息、常量池、类方法的定义（局部变量表）、虚拟机指令。

查看字节码内容指令：
javap -c -v /Users/super/IdeaProjects/awsome-jvm/jvm02/target/classes/com/charles/metaspace/StaticConstantPoolTest.class

```java
// 类基本信息
Classfile /Users/super/IdeaProjects/awsome-jvm/jvm02/target/classes/com/charles/metaspace/StaticConstantPoolTest.class
  Last modified 2021-8-18; size 750 bytes
  MD5 checksum e6ea597983ac72ce02654a8f02b01c4f
  Compiled from "StaticConstantPoolTest.java"
public class com.charles.metaspace.StaticConstantPoolTest
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
  // 常量池
Constant pool:
   #1 = Methodref          #6.#27         // java/lang/Object."<init>":()V
   #2 = String             #28            // a
   #3 = Fieldref           #29.#30        // java/lang/System.out:Ljava/io/PrintStream;
   #4 = Methodref          #31.#32        // java/io/PrintStream.println:(Z)V
   #5 = Class              #33            // com/charles/metaspace/StaticConstantPoolTest
   #6 = Class              #34            // java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               LocalVariableTable
  #12 = Utf8               this
  #13 = Utf8               Lcom/charles/metaspace/StaticConstantPoolTest;
  #14 = Utf8               main
  #15 = Utf8               ([Ljava/lang/String;)V
  #16 = Utf8               args
  #17 = Utf8               [Ljava/lang/String;
  #18 = Utf8               a1
  #19 = Utf8               Ljava/lang/String;
  #20 = Utf8               a2
  #21 = Utf8               StackMapTable
  #22 = Class              #17            // "[Ljava/lang/String;"
  #23 = Class              #35            // java/lang/String
  #24 = Class              #36            // java/io/PrintStream
  #25 = Utf8               SourceFile
  #26 = Utf8               StaticConstantPoolTest.java
  #27 = NameAndType        #7:#8          // "<init>":()V
  #28 = Utf8               a
  #29 = Class              #37            // java/lang/System
  #30 = NameAndType        #38:#39        // out:Ljava/io/PrintStream;
  #31 = Class              #36            // java/io/PrintStream
  #32 = NameAndType        #40:#41        // println:(Z)V
  #33 = Utf8               com/charles/metaspace/StaticConstantPoolTest
  #34 = Utf8               java/lang/Object
  #35 = Utf8               java/lang/String
  #36 = Utf8               java/io/PrintStream
  #37 = Utf8               java/lang/System
  #38 = Utf8               out
  #39 = Utf8               Ljava/io/PrintStream;
  #40 = Utf8               println
  #41 = Utf8               (Z)V
{
  public com.charles.metaspace.StaticConstantPoolTest();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 8: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/charles/metaspace/StaticConstantPoolTest;

  // 类方法的定义
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    // 虚拟机指令
    Code:
      stack=3, locals=3, args_size=1
         0: ldc           #2                  // String a
         2: astore_1
         3: ldc           #2                  // String a
         5: astore_2
         6: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
         9: aload_1
        10: aload_2
        11: if_acmpne     18
        14: iconst_1
        15: goto          19
        18: iconst_0
        19: invokevirtual #4                  // Method java/io/PrintStream.println:(Z)V
        22: return
      LineNumberTable:
        line 10: 0
        line 11: 3
        line 12: 6
        line 13: 22
      // 局部变量表
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      23     0  args   [Ljava/lang/String;
            3      20     1    a1   Ljava/lang/String;
            6      17     2    a2   Ljava/lang/String;
      StackMapTable: number_of_entries = 2
        frame_type = 255 /* full_frame */
          offset_delta = 18
          locals = [ class "[Ljava/lang/String;", class java/lang/String, class java/lang/String ]
          stack = [ class java/io/PrintStream ]
        frame_type = 255 /* full_frame */
          offset_delta = 0
          locals = [ class "[Ljava/lang/String;", class java/lang/String, class java/lang/String ]
          stack = [ class java/io/PrintStream, int ]
}
SourceFile: "StaticConstantPoolTest.java"
```

#### 3.5.2 静态常量池（Class常量池）

**Class字节码文件中的常量池**，用于存放编译器生成的字面量和符号引用。字面量就是我们所说的常量概念，如文本字符串String s = "a"、被声明为final的常量值等。 符号引用是一组符号来描述所引用的目标。

#### 3.5.3 运行时常量池

JVM虚拟机在完成类的加载操作后，将存放在class字节码文件中的常量池信息存放到运行时常量池中。

#### 3.5.3 字符串常量池

JVM为了提高性能和减少内存开销，提供了字符串常量池用于实例化字符串常量。字符串常量池存在于方法区。

##### 1）字符串对象的创建

**代码1：堆栈方法区存储字符串**

```java
String str1 = “abc”;
String str2 = “abc”;
String str3 = “abc”;
String str4 = new String(“abc”);
String str5 = new String(“abc”);
```
![堆栈方法区存储字符串][11]
面试题：String str4 = new String(“abc”) 创建多少个对象？
1. 在常量池中查找是否有“abc”对象，有则返回对应的引用实例，没有则创建对应的实例对象；
2. 在堆中 new 一个 String("abc") 对
3. 将对象地址赋值给str4,创建一个引用
所以，常量池中没有“abc”字面量则创建两个对象，否则创建一个对象，以及创建一个引用

根据字面量，往往会提出这样的变式题：
String str1 = new String("A"+"B") ; 会创建多少个对象?
String str2 = new String("ABC") + "ABC" ; 会创建多少个对象?

str1：
字符串常量池："A","B","AB" : 3个
堆：new String("AB") ：1个
引用： str1 ：1个
总共 ： 5个

str2 ：
字符串常量池："ABC" : 1个
堆：new String("ABC") ：1个
引用： str2 ：1个
总共 ： 3个

**代码2：基础类型的变量和常量，变量和引用存储在栈中，常量存储在常量池中**

```java
int a1 = 1;
int a2 = 1;
int a3 = 1;

public static int INT1 =1 ;
public static int INT2 =1 ;
public static int INT3 =1 ;
```
![基础类型的变量和常量][12]

##### 2）操作字符串常量池的方式

 - JVM实例化字符串常量池时
```java
  String str1 = “hello”;
  String str2 = “hello”;
  
  System.out.printl（"str1 == str2" : str1 == str2 ) //true
```

 - String.intern()
```java
        // Create three strings in three different ways.
        String s1 = "Hello";
        String s2 = new StringBuffer("He").append("llo").toString();
        String s3 = s2.intern();
 
        // Determine which strings are equivalent using the ==
        // operator
        System.out.println("s1 == s2? " + (s1 == s2)); // false
        System.out.println("s1 == s3? " + (s1 == s3)); // true

```

```java
public class StringConstantPoolTest {

    public static void main(String[] args) {
        // 1. 常量池的中的字符串仅是符号，第一次使用的时候变为对象
        String s1 = "a";
        // 2. 利用字符串常量池的机制，来避免重复创建字符串对象
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
```
### 3.6 程序计数器

记录当前线程执行下一行指令的执行地址，作用在多线程因上下文切换过程中记录当前线程下一行指令。

### 3.7 类加载器

读取class字节码文件到JVM虚拟机内存中。

------

## 4. GC日志分析


----------


## 5. 串行与并行收集器

## 6. CMS收集器

## 7. G1收集器

## 8. JVM参数调优

---


作者 @charles


  [1]: https://github.com/tgpsuccess/awsome-jvm#1-classloader%E7%B1%BB%E5%8A%A0%E8%BD%BD%E5%99%A8
  [2]: https://github.com/tgpsuccess/awsome-jvm#11-%E7%B1%BB%E5%8A%A0%E8%BD%BD%E5%99%A8%E5%88%86%E7%B1%BB
  [3]: https://github.com/tgpsuccess/awsome-jvm#12-%E7%B1%BB%E5%8A%A0%E8%BD%BD%E5%99%A8%E5%8F%8C%E4%BA%B2%E5%A7%94%E6%B4%BE%E6%9C%BA%E5%88%B6
  [4]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/%E5%BD%93%E5%89%8D%E7%BA%BF%E7%A8%8B%E9%BB%98%E8%AE%A4%E4%BD%BF%E7%94%A8%E5%BA%94%E7%94%A8%E7%B1%BB%E5%8A%A0%E8%BD%BD%E5%99%A8.png
  [5]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/SPI%E6%9C%BA%E5%88%B6.png
  [6]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/Mysql%E9%A9%B1%E5%8A%A8%E5%AE%9E%E7%8E%B0%E7%B1%BB%E5%88%9D%E5%A7%8B%E5%8C%96%E5%A4%B1%E8%B4%A5.png
  [7]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/%E5%A0%86%E5%86%85%E5%AD%98%E6%BA%A2%E5%87%BA.png
  [8]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/%E5%A0%86%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F.png
  [9]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/%E6%A0%88%E5%B8%A7%E6%B5%8B%E8%AF%95.png
  [10]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/%E6%A0%88%E5%86%85%E5%AD%98%E6%BA%A2%E5%87%BA.png
  [11]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/%E5%A0%86%E6%A0%88%E6%96%B9%E6%B3%95%E5%8C%BA%E5%AD%98%E5%82%A8%E5%AD%97%E7%AC%A6%E4%B8%B2.jpeg
  [12]: https://github.com/tgpsuccess/awsome-jvm/blob/master/docs/images/%E5%9F%BA%E7%A1%80%E7%B1%BB%E5%9E%8B%E7%9A%84%E5%8F%98%E9%87%8F%E5%92%8C%E5%B8%B8%E9%87%8F.jpeg