# Java 7 新特性

在经历了推迟发布，Sun 被收购，新特性删减，再推迟发布等一系列事件之后，JDK7 终于还是来到了大家的面前，那么 JDK7 到底有哪些重要的新特性呢？

- Java 语言特性的增强（JSR334）
   - 支持 String 的 switch 语句
   - try-with-resources
   - 多重 catch 的改进
   - 二进制字面量
   - 数值可以用下划线分割
   - 更简洁的泛型
   - @SafeVarargs
- JSR292：支持动态类型语言（InvokeDynamic）
- G1 垃圾回收器（Garbage-First Collector）
- 核心类库改进
    - ClassLoader 新增 API
    - URLClassLoader 新增 API
    - Concurrent 包的改进
    - 国际化（i18n）
- I/O 与网络
    - Java 平台的更多新 NIO 2 的 API（JSR 203）
    - 支持 zip/jar 的 FileSystemProvider 实现
    - SDP(Socket Direct Protocol)
    - 使用 Windows Vista 上的 IPv6 栈
- 图形界面客户端
    - Swing 的 Nimbus 外观感觉
    - JLayer
    - 混合重量级和轻量级组件
    - 不规则和透明窗体
- 其他模块
    - XML
    - Java 2D
    - 安全 / 加密
    - 数据库连接 （JDBC）



## 一、Java 语言特性的增强（JSR334）

### 1.1> Switch 语句中允许使用 String 类型：

在Java 7 中，Switch语句中允许使用String类型。实则是将switch/case语句中的字符串替换成了对应的哈希值，经过这样的转换，Java虚拟机所看到的仍然是与证书类型兼容的类型。

```java
switch(myString) { 
    case "one": <do something>; break; 
    case "two": <do something else>; break; 
    default: <do something generic>; 
}
```

### 1.2> 自动的资源管理机制 & 多重 catch 来处理多种异常类型：

try-with-resources 可以保证打开的流被正确关闭，而不需要finally语句来关闭流。能够被try语句管理的资源需要实现java.lang.AutoCloseable接口，否则会出现编译错误。

Java类库中有不少接口或类继承或实现了该接口：

- java.io.Closeable
- java.sql.Connection
- java.sql.ResultSet
- java.sql.Statement
 
Java 7 改进了catch子句的语法，允许在其中指定多种异常，异常间用“|”分割。多个异常间不能有包含关系，如：Exception和RuntimeException同时捕获，无法通过编译。

```java
public class MultipleResourcesUsage {
    public void copyFile(String fromPath, String toPath) {
        try (InputStream input = new FileInputStream(fromPath); OutputStream output = new FileOutputStream(toPath)) {
            byte[] buffer = new byte[8192];
            int len = -1;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        } catch (IOException | IOError e) {
            // TODO: handle exception
        }
    }
}
```

### 1.3> 二进制整数字面量

Java 7之前：

- 八进制：字面量前面加“0”
- 十六进制：字面量前面加“0X”或“0x”
 
Java 7新增：

- 二进制：字面量前面加“0B”或“0b”

```java
import static java.lang.System.out;
public class BinaryIntegralLiteral {
    public void display() {
        out.println(0b001001); // 输出 9
        out.println(0B001110); // 输出 14
    }
}
```

### 1.4> 数值可以用下划线分割：

在Java 7 中，数值字面量，不管是整数还是浮点数，都可以在数字之间插入任意多个下划线。下划线不会对字面量的数值产生影响，只是为了方便阅读。

> 需要注意的是：下划线只能出现在数值中间。“_100”、“100_”、“0b_101”、“0x_da0”这种使用方式都是错误的，无法通过编译

```java
import static java.lang.System.out;
public class Underscore {
    public void display() {
        double val1 = 3.1_4;
        int val2 = 1_1__0;
        out.println(val1); // 输出 3.14
        out.println(val2); // 输出 110
        out.println(1_100_100); // 输出 1100100
    }
}
```

### 1.5> 对通用类型实例的创建提供类型推理：

Java 7 把类型推断从方法调用扩展到了对象创建，即增加了“<>”操作符。在Java 7之前需要显示指定实际的类型。在Java 7得到了简化，可以直接使用“<>”来替代类型。

```java
public <T> List<T> createList() {
    return new ArrayList<T>();
}

//上面泛型自动推断出了类型为Integer
List<Interger> list = createList();

//Map<String，MyType> foo = new Map<String,MyType>(); 
Map <String,MyType> foo = new Map<>(); //等价于上面的代码
```

### 1.6> @SafeVarargs

如果开发人员确信某个使用了可变长度can参数的方法，在与泛型类一起使用时不会出现类型安全问题，可以使用@SafeVarargs注解进行声明，编译器就不会再给出相关的警告。

```java
@SafeVarargs
public static <T> T useVarargs(T... args) {
    return args.length > 0 ? args[0] : null;
}
```

## 二、JSR292 - 支持动态类型语言（InvokeDynamic）

JVM 已经成为了更多的动态语言 ( 例如：jruby、jython、fan、clojure、 … ) 的运行时环境，但由于 JVM 本身的设计原来是针对 Java 这种静态类型语言的，所以脚本语言无论是解释执行，或者是编译时用虚拟类型，还是运用反射机制，都会对执行效率产生很大程度的影响。在 Java 7 中，JSR292 的实现增加了一个 InvokeDynamic 的字节码指令来支持动态类型语言，使得在把源代码编译成字节码时并不需要确定方法的签名，即方法参数的类型和返回类型。当运行时执行 InvokeDynamic 指令时，JVM 会通过新的动态链接机制 Method Handles，寻找到真实的方法。

## 三、G1 垃圾回收器（Garbage-First Collector）

G1 垃圾回收器是一个服务器端的垃圾回收器，针对大内存多核 CPU 的环境，目的在于减少 Full GC 带来的暂停次数，增加吞吐量。实现上，G1 在堆上分配一系列相同大小的连续区域，然后在回收时先扫描所有的区域，按照每块区域内存活对象的大小进行排序，优先处理存活对象小的区域，即垃圾对象最多的区域，这也是 Garbage First 这个名称的由来。G1 把要收集的区域内的存活对象合并并且复制到其他区域，从而避免了 CMS 遇到的内存碎片问题。

## 四、核心类库改进

### 4.1> ClassLoader 新增 API

为了防止自定义多线程 ClassLoad 产生的死锁问题，java.lang.ClassLoader 类增加了以下 API。

```java
protected Object getClassLoadingLock(String className) 
protected static boolean registerAsParallelCapable()
```

### 4.2> URLClassLoader 新增 API

URLClassLoader 新增 close 方法可以关闭该类加载器打开的资源。

### 4.3> Concurrent 包的改进

java.util.concurrent 包引入了一个轻量级的 fork/join 的框架来支持多核多线程的并发计算。fork/join 框架采用了分治的技术和思想，获取问题后，递归的把整个大问题分成多个小的子问题，直到每个子问题都足够小，使得这些小的子问题都可以高效的解决，然后把这些子问题放入队列中等待处理（Fork 的过程），接下来等待所有子问题的结果（Join 的过程），把多个结果合并到一起。

其他的增强：

- TransferQueue –使得生产者 / 消费者队列模型更加有效，TransferQueue 是一种 BlockingQueue，但其不同之处是提供了一个记录 的交付服务；虽然将对象成功添加到队列中之后会返回一个将对象插入 BlockingQueue 的线程，但是仅在另一个线程从队列里删除了对象之后才会返回负责将对象插入到 TransferQueue 中的线程。
- Phaser – 引入了一个全新灵活的线程同步机制，如果你喜欢等待线程结束然后继续执行其他任务，那么 Phaser 是一个好的选择。

### 4.4> 国际化（i18n）

支持 Unicode 6.0。改进 java.util.Locale 以支持 IETF BCP 47 和 UTR 35，并且在 get/set locale 的时候分成了用于显示的 locale 和用于格式化的 locale。

## 五、I/O 与网络

### 5.1> Java 平台的更多新 NIO 2 的 API（JSR 203）

新的文件系统接口、支持大块访问文件属性、更改通知、绕开文件系统指定的 API，也是可插拔文件系统实现的服务提供者接口； 对套接字和文件同时提供了异步 I/O 操作的 API。

所有的 异步I/O 操作都有下列 2 种形式中的一种：

- 第一个返回 java.util.concurent.Future, 代表等待结果，可使用 Future 特性等待 I/O 操作 ;
- 第二个是使用 CompletionHandler 创建的，当操作结束时，如回调系统，调用这个处理程序

Socket 和文件的异步 IO。

Socket channel 的功能完善，支持 binding、多播等。

NIO.2 中所有的异步 channel 如下：

- AsynchronousFileChannel：读写文件异步通道 ;
- AsynchronousSocketChannel：用于套接字的一个简单异步通道 ;
- AsynchronousServerSocketChannel：用户 ServerSocket 的异步通道，accept() 方法是异步的，连接被接受时，调用 CompletionHandler;
- AsynchronousDatagramChannel：数据报套接字的异步通道 .

### 5.2> 支持 zip/jar 的 FileSystemProvider 实现

NIO2 提供了新的 service provider java.nio.file.spi.FileSystemProvider 来实现一个文件系统。实现了 SCTP (Stream Control Transmission Protocol) 协议，即流控制传输协议，由 RFC 2960 规范。它是一种类似于 TCP 的可靠传输协议。SCTP 在两个端点之间提供稳定、有序的数据传递服务（非常类似于 TCP），并且可以保护数据消息边界（例如 UDP）。然而，与 TCP 和 UDP 不同，SCTP 是通过多宿主（Multi-homing）和多流（Multi-streaming）功能提供这些收益的，这两种功能均可提高可用性 。

### 5.3> SDP(Socket Direct Protocol)

SDP，套接字定向协议，提供了高吞吐量低延迟的高性能网络连接。它的设计目标是为了使得应用程序能够透明地利用 RDMA(Remote Direct Memory Access) 通信机制来加速传统的 TCP/IP 网络通信。最初 SDP 由 Infiniband 行业协会的软件工作组所指定，主要针对 Infiniband 架构，后来 SDP 发展成为利用 RDMA 特性进行传输的重要协议。JDK7 这次实现 Solaris 和 Linux 平台上的 SDP。

### 5.4> 使用 Windows Vista 上的 IPv6 栈

更新了网络方面的代码，在 Windows Vista 上，当 IPv6 栈可用时，优先使用 IPv6 栈。

## 六、图形界面客户端

### 6.1> Swing 的 Nimbus 外观感觉

Nimbus 是 Swing 上新一代的跨平台外观感觉 (Look & Feel)。其实 Nimbus 在 Java 6 中已经存在，但直到 Java 7 才被移到了标准 Swing 的名字空间（javax.swing）。原来 Java 中的默认的跨平台外观感觉是“金属”（Metal）或者被称为 Java 外观感觉。Nimbus 起初作为一个开源的项目，它使用 Java2D 矢量绘图而不是点阵图片来渲染图形界面控件，因而使得图形界面控件可以精确地被任意缩放。这个特性特别符合现代富客户端图形控件的发展趋势。

[Numbus 官方教程](https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html)

### 6.2> JLayer

通常情况下，自定义图形控件的绘制需要覆写控件的绘图方法，但是很多情况下这不是一个好办法。于是，JLayer 类应运而生，它可以被装饰在已有的 Swing 组件上。这样界面组件不需要被修改就可以完成自定义渲染和事件响应。一个例子是给一个窗口的所有控件装饰自定义的背景，比如模糊界面所有像素。

[JLayer 官方教程](http://docs.oracle.com/javase/tutorial/uiswing/misc/jlayer.html)

### 6.3> 混合重量级和轻量级组件

在 Java 图形控件中有两类，重量级（heavyweight）和轻量级（lightweight）控件。轻量级控件没有对应的操作系统本地控件，比如大多数 Swing 控件：JLabel 和 JButton。重量级控件则相反，对应于本地控件，比如 AWT 的 Button 和 Label。历史上，在一个窗体里混合使用重量级和轻量级控件存在问题，特别是它们互相重叠的时候。现在，Java 7 中混合使用变得比较方便。

### 6.4> 不规则和透明窗体

Java 7 中正式将创建不规则和透明窗体的 API 引入了公开的 AWT 包。当然，这些很炫的功能需要系统底层图形界面的支持。以下列出了相关 API：

- GraphicsDevice.isWindowTranslucencySupported(WindowTranslucency)
- GraphicsConfiguration.isTranslucencyCapable()
- Window.setOpacity(float)
- Window.setShape(Shape)
- Window.setBackground(Color)

[参考教程](https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html)

## 七、其他模块

### 7.1> XML

将最新的 XML 组件更新到相关开源实现的稳定版本：JAXP 1.4、JAXB 2.2a、JAX-WS 2.2。

### 7.1> Java 2D

- 对于现代 X11 桌面系统，提供了基于 XRender 的渲染管线。
- 加入了 OpenType/CFF 字体的支持。
- 对 Linux 字体更好的支持，使用 libfontconfig 来选择字体。

### 7.1> 安全 / 加密

- 椭圆曲线加密算法 (ECC)，提供了一个可移植的标准椭圆曲线加密算法实现，所有的 Java 应用都可以使用椭圆曲线加密算法
- JSSE(SSL/TLS)
    - 在证书链认证中设置关闭弱加密算法，比如 MD2 算法已经被证实不太安全。
    - 增加对 TLS(Transport Layer Security) 1.1 和 1.2 的支持，它们对应的规范分别是 RFC 4346 和 RFC 5246。
    - SNI(Server Name Indication) 支持，其规范定义在 RFC 4366。
    - TLS 密钥重新协商机制，RFC 5746。

### 7.1> 数据库连接 （JDBC）

支持了规范 JDBC 4.1 和 Rowset 1.1。

---

参考资源：

[JDK 7 新特性 - 总览](https://www.ibm.com/developerworks/cn/java/j-lo-jdk7-1/index.html)