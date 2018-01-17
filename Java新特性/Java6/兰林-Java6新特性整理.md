# java 6 新特性

> Java 6 在性能方面有了不错的提升。与 Java 5 在 API 库方面的大幅度加强相比，虽然在 API 库方面的新特性显得不太多，但是也提供了许多实用和方便的功能

- [Instrumentation 新功能](#instrumentation)
- [HTTP 增强](#http)
- [JMX 与系统管理](#jmx)
- [编译器 API](#api) 
- [Java DB 和 JDBC 4.0](#db)
- [对脚本语言的支持](#script)
- [XML API 与 Web 服务](#xml)

<a id='instrumentation'></a>

## 一、Instrumentation 新功能

### Instrumentation 简介

利用 Java 代码，即 `java.lang.instrument` 做动态 `Instrumentation` 是 Java 5 的新特性，它把 Java 的 instrument 功能从本地代码中解放出来，使之可以用 Java 代码的方式解决问题。使用 `Instrumentation`，开发者可以构建一个独立于应用程序的代理程序（Agent），用来监测和协助运行在 JVM 上的程序，甚至能够替换和修改某些类的定义。有了这样的功能，开发者就可以实现更为灵活的运行时虚拟机监控和 Java 类操作了，这样的特性实际上提供了一种虚拟机级别支持的 AOP 实现方式，使得开发者无需对 JDK 做任何升级和改动，就可以实现某些 AOP 的功能了。

在 Java 6 里面，instrumentation 包被赋予了更强大的功能：启动后的 instrument、本地代码（native code）instrument，以及动态改变 classpath 等等。这些改变，意味着 Java 具有了更强的动态控制、解释能力，它使得 Java 语言变得更加灵活多变。

在 Java 6 里面，最大的改变使运行时的 `Instrumentation` 成为可能。在 Java 5 中，Instrument 要求在运行前利用命令行参数或者系统参数来设置代理类，在实际的运行之中，虚拟机在初始化之时（在绝大多数的 Java 类库被载入之前），instrumentation 的设置已经启动，并在虚拟机中设置了回调函数，检测特定类的加载情况，并完成实际工作。但是在实际的很多的情况下，我们没有办法在虚拟机启动之时就为其设定代理，这样实际上限制了 instrument 的应用。而 Java 6 的新特性改变了这种情况，通过 Java Tool API 中的 attach 方式，我们可以很方便地在运行过程中动态地设置加载代理类，以达到 instrumentation 的目的。

另外，对 native 的 `Instrumentation` 也是 Java 6 的一个崭新的功能，这使以前无法完成的功能 —— 对 native 接口的 instrumentation 可以在 Java 6 中，通过一个或者一系列的 prefix 添加而得以完成。

最后，Java 6 里的 `Instrumentation` 也增加了动态添加 class path 的功能。所有这些新的功能，都使得 instrument 包的功能更加丰富，从而使 Java 语言本身更加强大。

### Instrumentation 基本功能和用法

`java.lang.instrument`包的具体实现，依赖于 `JVMTI`。`JVMTI（Java Virtual Machine Tool Interface）`是一套由 Java 虚拟机提供的，为 JVM 相关的工具提供的本地编程接口集合。`JVMTI` 是从 Java 5 开始引入，整合和取代了以前使用的 `Java Virtual Machine Profiler Interface (JVMPI)` 和 `the Java Virtual Machine Debug Interface (JVMDI)`，而在 Java 6 中，`JVMPI` 和 `JVMDI` 已经消失了。`JVMTI` 提供了一套”代理”程序机制，可以支持第三方工具程序以代理的方式连接和访问 JVM，并利用 `JVMTI` 提供的丰富的编程接口，完成很多跟 JVM 相关的功能。

事实上，`java.lang.instrument` 包的实现，也就是基于这种机制的：在 `Instrumentation` 的实现当中，存在一个 `JVMTI` 的代理程序，通过调用 `JVMTI` 当中 Java 类相关的函数来完成 Java 类的动态操作。除开 `Instrumentation` 功能外，`JVMTI` 还在虚拟机内存管理，线程控制，方法和变量操作等等方面提供了大量有价值的函数。

`Instrumentation` 的最大作用，就是类定义动态改变和操作。在 Java 5 及其后续版本当中，开发者可以在一个普通 Java 程序（带有 main 函数的 Java 类）运行时，通过`– javaagent`参数指定一个特定的 jar 文件（包含 `Instrumentation` 代理）来启动 `Instrumentation` 的代理程序。

在 Java 5 当中，开发者可以让 `Instrumentation` 代理在 main 函数运行前执行。如下几个步骤：

**1、编写 premain 函数**

编写一个 Java 类，包含如下两个方法当中的任何一个

```java
public static void premain(String agentArgs, Instrumentation inst);  //[1]
public static void premain(String agentArgs); //[2]
```

其中，[1] 的优先级比 [2] 高，将会被优先执行（[1] 和 [2] 同时存在时，[2] 被忽略）

在这个 premain 函数中，开发者可以进行对类的各种操作。

agentArgs 是 premain 函数得到的程序参数，随同 `– javaagent`一起传入。与 main 函数不同的是，这个参数是一个字符串而不是一个字符串数组，如果程序参数有多个，程序将自行解析这个字符串。

inst 是一个 `java.lang.instrument.Instrumentation` 的实例，由 JVM 自动传入。`java.lang.instrument.Instrumentation` 是 instrument 包中定义的一个接口，也是这个包的核心部分，集中了其中几乎所有的功能方法，例如类定义的转换和操作等等。

**2、jar文件打包**

将这个 Java 类打包成一个 jar 文件，并在其中的 manifest 属性当中加入` Premain-Class`来指定步骤 1 当中编写的那个带有 premain 的 Java 类。（可能还需要指定其他属性以开启更多功能）

**3、运行**

用如下方式运行带有 `Instrumentation` 的 Java 程序：

> java -javaagent:jar 文件的位置 [= 传入 premain 的参数 ]

对 Java 类文件的操作，可以理解为对一个 byte 数组的操作（将类文件的二进制字节流读入一个 byte 数组）。开发者可以在`ClassFileTransformer`的**transform** 方法当中得到，操作并最终返回一个类的定义（一个 byte 数组）

Instrumentation 的基本使用方法的简单示例：

定义一个类TransClass，静态方法返回 1

```java
public class TransClass { 
    public int getNumber() { 
        return 1; 
    } 
}
```

定义一个测试类 TestMainInJar

```java
public class TestMainInJar { 
    public static void main(String[] args) { 
        System.out.println(new TransClass().getNumber()); 
    } 
}
```

然后，我们将 TransClass 的 getNumber 方法的返回值改为2，编译成类文件，命名为TransClass.class.2，再将返回值改回1

接下来，建立一个Transformer 类：

```java
public class Transformer implements ClassFileTransformer {

    public static final String classNumberReturns2 = "TransClass.class.2";

    public static byte[] getBytesFromFile(String fileName) {
        try {
            // precondition 
            File file = new File(fileName);
            InputStream is = new FileInputStream(file);
            long length = file.length();
            byte[] bytes = new byte[(int) length];

            // Read in the bytes 
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            is.close();
            return bytes;
        } catch (Exception e) {
            System.out.println("error occurs in _ClassTransformer!" + e.getClass().getName());
            return null;
        }
    }

    public byte[] transform(ClassLoader l, String className, Class<?> c, ProtectionDomain pd, byte[] b) throws IllegalClassFormatException {
        if (!className.equals("TransClass")) {
            return null;
        }
        return getBytesFromFile(classNumberReturns2);
    }

}
```

这个类实现了 `ClassFileTransformer` 接口。其中，`getBytesFromFile` 方法根据文件名读入二进制字符流，而 `ClassFileTransformer` 当中规定的 `transform` 方法则完成了类定义的替换转换

最后，我们建立一个 Premain 类，写入 Instrumentation 的代理方法 premain：

```java
public class Premain {
    public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException {
        inst.addTransformer(new Transformer());
    }
}
```

代码完成后，我们将他们打包为 TestInstrument1.jar。返回 1 的那个 TransClass 的类文件保留在 jar 包中，而返回 2 的那个 TransClass.class.2 则放到 jar 的外面。在 manifest 里面加入如下属性来指定 premain 所在的类：

```
Manifest-Version: 1.0 
Premain-Class: Premain
```

在运行这个程序的时候，如果我们用普通方式运行这个 jar 中的 main 函数，可以得到输出 “1”。如果用下列方式运行 : 则会输出 “2”

> java – javaagent:TestInstrument1.jar – cp TestInstrument1.jar TestMainInJar

除开用 addTransformer 的方式，Instrumentation 当中还有另外一个方法“redefineClasses”来实现 premain 当中指定的转换。用法类似，如下：

```java
public class Premain {
    public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException {
        ClassDefinition def = new ClassDefinition(TransClass.class,
                Transformer.getBytesFromFile(Transformer.classNumberReturns2));
        inst.redefineClasses(new ClassDefinition[] { def });
        System.out.println("success");
    }
}
```

`redefineClasses` 的功能比较强大，可以批量转换很多类。

### 新特性：虚拟机启动后的动态 instrument

在 Java 5 当中，开发者所作的 Instrumentation 仅限与 main函数执行前，这样的方式存在一定的局限性。在 Java 6 的 Instrumentation 当中，有一个跟 `premain`“并驾齐驱”的`agentmain`方法，可以在 main 函数开始运行之后再运行。

在 Java SE 6 的新特性里面，有一个不太起眼的地方，揭示了 agentmain 的用法。这就是 Java SE 6 当中提供的 Attach API。

`TransClass` 类和 `Transformer` 类的代码不变，参看上一节介绍。 含有 main 函数的 `TestMainInJar` 代码为：

```
public class TestMainInJar { 
    public static void main(String[] args) throws InterruptedException { 
        System.out.println(new TransClass().getNumber()); 
        int count = 0; 
        while (true) { 
            Thread.sleep(500); 
            count++; 
            int number = new TransClass().getNumber(); 
            System.out.println(number); 
            if (3 == number || count >= 10) { 
                break; 
            } 
        } 
    } 
}
```

含有 `agentmain` 的 `AgentMain` 类的代码为：

```java
public class AgentMain { 
   public static void agentmain(String agentArgs, Instrumentation inst) 
           throws ClassNotFoundException, UnmodifiableClassException, 
           InterruptedException { 
       inst.addTransformer(new Transformer (), true); 
       inst.retransformClasses(TransClass.class); 
       System.out.println("Agent Main Done"); 
   } 
}
```

其中，`retransformClasses` 是 Java 6 里面的新方法，它跟 `redefineClasses` 一样，可以批量转换类定义，多用于 agentmain 场合

Jar 文件跟 Premain 那个例子里面的 Jar 文件差不多，也是把 main 和 agentmain 的类，TransClass，Transformer 等类放在一起，打包为“TestInstrument1.jar”，而 Jar 文件当中的 Manifest 文件为 :

```
Manifest-Version: 1.0 
Agent-Class: AgentMain
```

另外，为了运行Attach API再写一个控制程序来模拟监控过程：

```java
import com.sun.tools.attach.VirtualMachine; 
import com.sun.tools.attach.VirtualMachineDescriptor; 

static class AttachThread extends Thread {
    private final List<VirtualMachineDescriptor> listBefore;

    private final String jar;

    AttachThread(String attachJar, List<VirtualMachineDescriptor> vms) {
        listBefore = vms; // 记录程序启动时的 VM 集合
        jar = attachJar;
    }

    public void run() {
        VirtualMachine vm = null;
        List<VirtualMachineDescriptor> listAfter = null;
        try {
            int count = 0;
            while (true) {
                listAfter = VirtualMachine.list();
                for (VirtualMachineDescriptor vmd : listAfter) {
                    if (!listBefore.contains(vmd)) {
                        // 如果 VM 有增加，我们就认为是被监控的 VM 启动了
                        // 这时，我们开始监控这个 VM 
                        vm = VirtualMachine.attach(vmd);
                        break;
                    }
                }
                Thread.sleep(500);
                count++;
                if (null != vm || count >= 10) {
                    break;
                }
            }
            vm.loadAgent(jar);
            vm.detach();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new AttachThread("TestInstrument1.jar", VirtualMachine.list()).start();
    }
}
```

运行时，可以首先运行上面这个启动新线程的 main 函数，然后，在 5 秒钟内（仅仅简单模拟 JVM 的监控过程）运行如下命令启动测试 Jar 文件

> java – javaagent:TestInstrument2.jar – cp TestInstrument2.jar TestMainInJar

预期结果：程序首先会在屏幕上打出 1，这是改动前的类的输出，然后会打出一些 2，这个表示 agentmain 已经被 Attach API 成功附着到 JVM 上，代理程序生效了，当然，还可以看到“Agent Main Done”字样的输出

### 新特性：本地方法的 Instrumentation

在 1.5 版本的 instumentation 里，并没有对 Java 本地方法（Native Method）的处理方式，而且在 Java 标准的 JVMTI 之下，并没有办法改变 method signature， 这就使替换本地方法非常地困难

在 Java 6 中，有了对 native 代码的 instrument 方式 —— 设置 prefix

假设我们有了一个 native 函数，在运行中过程中，我们需要将它指向另外一个函数（需要注意的是，在当前标准的 JVMTI 之下，除了 native 函数名，其他的 signature 需要一致）。比如：

```java
class nativePrefixTester{ 
    native int nativeMethod(int input); 
}
```

已经实现的本地代码是：

> jint Java_nativeTester_nativeMethod(jclass thiz, jobject thisObj, jint input);

需要在调用时，指向另外一个函数。按照他的命名方式，加上一个prefix作为新的函数名。比如：

> jint Java_nativeTester_another_nativePrefixTester(jclass thiz, jobject thisObj, jint input);

有了新的本地函数，接下来做的就是instrument。可以使用premain方式，在虚拟机启动之时就载入premain完成instrument代理设置。也可以使用agentmain方式，去attach虚拟机来启动代理。

```java
premain(){  // 或者也可以在 agentmain 里
    if (!isNativeMethodPrefixSupported()){ 
         return; // 如果无法设置，则返回
    } 
    setNativeMethodPrefix(transformer,"another_"); // 设置 native 函数的 prefix，注意这个下划线必须由用户自己规定
}
```

**这里要注意两个问题:**

1.agent 包之中的 Manifest 所设定的特性（影响是否可以设置 native prefix，如果给一个不是“true”的值，就会被当作 false 值处理）:

> Can-Set-Native-Method-Prefix

2.可以为每一个 ClassTransformer 加上它自己的 nativeprefix；同时，每一个 ClassTransformer 都可以为同一个 class 做 transform，因此对于一个 Class 来说，一个 native 函数可能有不同的 prefix，因此对这个函数来说，它可能也有好几种解析方式

由Java的函数接口`native void method()`和上述prefix“another”，去寻找本地代码中的函数`void Java_package_class_another_method(jclass theClass, jobject thiz);`

可以找到则调用这个函数，解析结束；如果找不到，那么虚拟机将会做进一步的解析工作，利用Java native接口最基本的解析方式去找本地代码中的函数`void Java_package_class_method(jclass theClass, jobject thiz);`如果找到则执行，否则解析失败

### 新特性：BootClassPath / SystemClassPath 的动态增补

通过设置系统参数或者通过虚拟机启动参数，可以设置一个虚拟机运行时的 boot class 加载路径（-Xbootclasspath）和 system class（-cp）加载路径。当然，我们在运行之后无法替换它。然而，我们也许有时候要需要把某些 jar 加载到 bootclasspath 之中，而我们无法应用上述两个方法，或者我们需要在虚拟机启动之后来加载某些 jar 进入 bootclasspath。在 Java SE 6 之中，我们可以做到这一点了。

首先，我们依然需要确认虚拟机已经支持这个功能，然后在 premain/agantmain 之中加上需要的 classpath。我们可以在我们的 Transformer 里使用 appendToBootstrapClassLoaderSearch/appendToSystemClassLoaderSearch 来完成这个任务。

同时我们可以注意到，在 agent 的 manifest 里加入 Boot-Class-Path 其实一样可以在动态地载入 agent 的同时加入自己的 boot class 路径，当然，在 Java code 中它可以更加动态方便和智能地完成 —— 我们可以很方便地加入判断和选择成分。

有几点需要注意：

1.加入到 classpath 的 jar 文件中不应当带有任何和系统的 instrumentation 有关的系统同名类，不然，一切都陷入不可预料之中

2.要注意到虚拟机的 ClassLoader 的工作方式，它会记载解析结果。之前读取某个Class失败了，后面动态加入了，ClassLoader依然会认为无法读取这个Class

3.在 Java 语言中有一个系统参数“java.class.path”，这个 property 里面记录了我们当前的 classpath，但是，我们使用这两个函数，虽然真正地改变了实际的 classpath，却不会对这个 property 本身产生任何影响

<a id='http'></a>

## 二、HTTP 增强

### 概述

Java 语言从诞生的那天起，就非常注重网络编程方面的应用。随着互联网应用的飞速发展，Java 的基础类库也不断地对网络相关的 API 进行加强和扩展。在 Java SE 6 当中，围绕着 HTTP 协议出现了很多实用的新特性：NTLM 认证提供了一种 Window 平台下较为安全的认证机制；JDK 当中提供了一个轻量级的 HTTP 服务器；提供了较为完善的 HTTP Cookie 管理功能；更为实用的 NetworkInterface；DNS 域名的国际化支持等等

### NTLM 认证

网络中有很多资源是被安全域保护起来的。访问这些资源需要对用户的身份进行认证。下面是一个简单的例子:

```java
import java.net.*; 
import java.io.*; 
 
public class Test { 
    public static void main(String[] args) throws Exception { 
        URL url = new URL("http://PROTECTED.com"); 
        URLConnection connection = url.openConnection(); 
        InputStream in = connection.getInputStream(); 
        byte[] data = new byte[1024]; 
        while(in.read(data)>0) { 
            //do something for data 
        } 
        in.close(); 
    } 
}
```

当程序试图从URLConnection的InputStream中read数据时，会引发FileNotFoundException。要解决这个问题有两种方法：

1.给URLConnection设定一个“Authentication”属性（假设 http://PROTECTED.COM 使用了基本（Basic）认证类型）：

```java
String credit = USERNAME + ":" + PASSWORD;
String encoding = new sun.misc.Base64Encoder().encode(credit.getBytes());
connection.setRequestProperty("Authorization", "Basic " + encoding);
```

2.使用 java.net.Authentication 类

提供一个继承于 Authentication 的类，实现 getPasswordAuthentication 方法，在 PasswordAuthentication 中给出用户名和密码

```java
class DefaultAuthenticator extends Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("USER", "PASSWORD".toCharArray());
    }
}
```

然后，将他设为默认的Authentication：

```java
Authenticator.setDefault(new DefaultAuthenticator());
```

Authentication 提供了关于认证发起者的足够多的信息，让继承类根据这些信息进行判断，在 getPasswordAuthentication 方法中给出了不同的认证信息：

- getRequestingHost()
- getRequestingPort()
- getRequestingPrompt()
- getRequestingProtocol()
- getRequestingScheme()
- getRequestingURL()
- getRequestingSite()
- getRequestorType()

不同的认证类型需要 Authentication 执行不同的协议。至 Java 6 为止，Authentication 支持的认证方式有：

- HTTP Basic authentication
- HTTP Digest authentication
- **NTLM**
- Http SPNEGO Negotiate
    - Kerberos
    - **NTLM**

NTLM 是 NT LAN Manager 的缩写。早期的 SMB 协议在网络上明文传输口令，这是很不安全的。微软随后提出了 WindowsNT 挑战 / 响应验证机制，即 NTLM

NTLM 协议是这样的：
1. 客户端首先将用户的密码加密成为密码散列；
2. 客户端向服务器发送自己的用户名，这个用户名是用明文直接传输的；
3. 服务器产生一个 16 位的随机数字发送给客户端，作为一个 challenge（挑战） ；
4. 客户端用步骤 1 得到的密码散列来加密这个 challenge ，然后把这个返回给服务器；
5. 服务器把用户名、给客户端的 challenge 、客户端返回的 response 这三个东西，发送域控制器 ；
6. 域控制器用这个用户名在 SAM 密码管理库中找到这个用户的密码散列，然后使用这个密码散列来加密 challenge；
7. 域控制器比较两次加密的 challenge ，如果一样，那么认证成功；

Java 6 以前的版本，是不支持 NTLM 认证的。用户若想使用 HttpConnection 连接到一个使用有 Windows 域保护的网站时，是无法通过 NTLM 认证的。另一种方法，是用户自己用 Socket 这样的底层单元实现整个协议过程，这无疑是十分复杂的。

终于，Java 6 的 Authentication 类提供了对 NTLM 的支持。使用十分方便，就像其他的认证协议一样：

```java
class DefaultAuthenticator extends Authenticator { 
    private static String username = "username "; 
    private static String domain =  "domain "; 
    private static String password =  "password "; 
   
    public PasswordAuthentication getPasswordAuthentication() {
        String usernamewithdomain = domain + "/ "+username; 
        return (new PasswordAuthentication(usernamewithdomain, password.toCharArray()));
    } 
}
```

根据 Windows 域账户的命名规范，账户名为域名 +”/”+ 域用户名

Java 6 中 Authentication 的另一个特性是认证协商。目前的服务器一般同时提供几种认证协议，根据客户端的不同能力，协商出一种认证方式。比如，IIS 服务器会同时提供 NTLM with kerberos 和 NTLM 两种认证方式，当客户端不支持 NTLM with kerberos 时，执行 NTLM 认证

目前，Authentication 的默认协商次序是：

> GSS/SPNEGO -> Digest -> NTLM -> Basic

### 轻量级 HTTP 服务器

Java 6 还提供了一个轻量级的纯 Java Http 服务器的实现。下面是一个简单的例子：

```java
public static void main(String[] args) throws Exception{ 
    HttpServerProvider httpServerProvider = HttpServerProvider.provider(); 
    InetSocketAddress addr = new InetSocketAddress(7778); 
    HttpServer httpServer = httpServerProvider.createHttpServer(addr, 1); 
    httpServer.createContext("/myapp/", new MyHttpHandler()); 
    httpServer.setExecutor(null); 
    httpServer.start(); 
    System.out.println("started"); 
} 
 
static class MyHttpHandler implements HttpHandler{ 
    public void handle(HttpExchange httpExchange) throws IOException {          
        String response = "Hello world!"; 
        httpExchange.sendResponseHeaders(200, response.length()); 
        OutputStream out = httpExchange.getResponseBody(); 
        out.write(response.getBytes()); 
        out.close(); 
    }  
}
```

在浏览器中访问http://localhost:7778/myapp/，看到Hello world!

首先，HttpServer 是从 HttpProvider 处得到的，这里我们使用了 JDK 6 提供的实现。用户也可以自行实现一个 HttpProvider 和相应的 HttpServer 实现。

其次，HttpServer 是有上下文（context）的概念的。比如，http://localhost:7778/myapp/ 中“/myapp/”就是相对于 HttpServer Root 的上下文。对于每个上下文，都有一个 HttpHandler 来接收 http 请求并给出回答。

最后，在 HttpHandler 给出具体回答之前，一般先要返回一个 Http head。这里使用 HttpExchange.sendResponseHeaders(int code, int length)。其中 code 是 Http 响应的返回值，比如那个著名的 404。length 指的是 response 的长度，以字节为单位。

### Cookie 管理特性

Cookie 是 Web 应用当中非常常用的一种技术， 用于储存某些特定的用户信息。虽然，我们不能把一些特别敏感的信息存放在 Cookie 里面，但是，Cookie 依然可以帮助我们储存一些琐碎的信息，帮助 Web 用户在访问网页时获得更好的体验，例如个人的搜索参数，颜色偏好以及上次的访问时间等等。网络程序开发者可以利用 Cookie 来创建有状态的网络会话（Stateful Session）。 Cookie 的应用越来越普遍。在 Windows 里面，我们可以在“Documents And Settings”文件夹里面找到 IE 使用的 Cookie，假设用户名为 admin，那么在 admin 文件夹的 Cookies 文件夹里面，我们可以看到名为“admin@(domain)”的一些文件，其中的 domain 就是表示创建这些 Cookie 文件的网络域， 文件里面就储存着用户的一些信息

JavaScript 等脚本语言对 Cookie 有着很不错的支持。 .NET 里面也有相关的类来支持开发者对 Cookie 的管理。 不过，在 Java 6 之前， Java 一直都没有提供 Cookie 管理的功能。在 Java 5 里面， java.net 包里面有一个 CookieHandler 抽象类，不过并没有提供其他具体的实现。到了 Java 6， Cookie 相关的管理类在 Java 类库里面才得到了实现。有了这些 Cookie 相关支持的类，Java 开发者可以在服务器端编程中很好的操作 Cookie， 更好的支持 HTTP 相关应用，创建有状态的 HTTP 会话。

- **用 HttpCookie 代表 Cookie** java.net.HttpCookie 类是 Java SE 6 新增的一个表示 HTTP Cookie 的新类， 其对象可以表示 Cookie 的内容（这个类储存了 Cookie 的名称，路径，值，协议版本号，是否过期，网络域，最大生命期等等信息）， 可以支持所有三种 Cookie 规范：
    - Netscape 草案
    - RFC 2109 - http://www.ietf.org/rfc/rfc2109.txt
    - RFC 2965 - http://www.ietf.org/rfc/rfc2965.txt
- **用 CookiePolicy 规定 Cookie 接受策略** java.net.CookiePolicy 接口可以规定 Cookie 的接受策略。 其中唯一的方法用来判断某一特定的 Cookie 是否能被某一特定的地址所接受。 这个类内置了 3 个实现的子类。一个类接受所有的 Cookie，另一个则拒绝所有，还有一个类则接受所有来自原地址的 Cookie。
- **用 CookieStore 储存 Cookie** java.net.CookieStore 接口负责储存和取出 Cookie。 当有 HTTP 请求的时候，它便储存那些被接受的 Cookie； 当有 HTTP 回应的时候，它便取出相应的 Cookie。 另外，当一个 Cookie 过期的时候，它还负责自动删去这个 Cookie。
- **用 CookieManger/CookieHandler 管理 Cookie** java.net.CookieManager 是整个 Cookie 管理机制的核心，它是 CookieHandler 的默认实现子类。一个 CookieManager 里面有一个 CookieStore 和一个 CookiePolicy，分别负责储存 Cookie 和规定策略。用户可以指定两者，也可以使用系统默认的 CookieManger。下图显示了整个 HTTP Cookie 管理机制的结构：<br/>![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/Java%E6%96%B0%E7%89%B9%E6%80%A7/Java6/fig002.jpg)
- 下面这个简单的例子说明了 Cookie 相关的管理功能：
 
```java
// 创建一个默认的 CookieManager 
CookieManager manager = new CookieManager(); 
 
// 将规则改掉，接受所有的 Cookie 
manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL); 
 
// 保存这个定制的 CookieManager 
CookieHandler.setDefault(manager); 
        
// 接受 HTTP 请求的时候，得到和保存新的 Cookie 
HttpCookie cookie = new HttpCookie("...(name)...","...(value)..."); 
manager.getCookieStore().add(uri, cookie); 
        
// 使用 Cookie 的时候：
// 取出 CookieStore        
CookieStore store = manager.getCookieStore(); 
 
// 得到所有的 URI        
List<URI> uris = store.getURIs(); 
for (URI uri : uris) { 
    // 筛选需要的 URI 
    // 得到属于这个 URI 的所有 Cookie 
    List<HttpCookie> cookies = store.get(uri); 
    for (HttpCookie cookie : cookies) { 
        // 取出了 Cookie 
    } 
} 
        
// 或者，取出这个 CookieStore 里面的全部 Cookie 
// 过期的 Cookie 将会被自动删除
List<HttpCookie> cookies = store.getCookies(); 
for (HttpCookie cookie : cookies) { 
    // 取出了 Cookie 
}
```

### 其他新特性

**NetworkInterface 的增强**

从 Java 1.4 开始，JDK 当中出现了一个网络工具类 java.net.NetworkInterface，提供了一些网络的实用功能。 在 Java 6 当中，这个工具类得到了很大的加强，新增了很多实用的方法。例如：

- public boolean isUp() 用来判断网络接口是否启动并运行
- public boolean isLoopback() 用来判断网络接口是否是环回接口（loopback）
- public boolean isPointToPoint() 用来判断网络接口是否是点对点（P2P）网络
- public boolean supportsMulticast() 用来判断网络接口是否支持多播
- public byte[] getHardwareAddress() 用来得到硬件地址（MAC）
- public int getMTU() 用来得到最大传输单位（MTU，Maximum Transmission Unit）
- public boolean isVirtual() 用来判断网络接口是否是虚拟接口
 
**域名的国际化**

一些 RFC 文档当中，规定 DNS 服务器可以解析除开 ASCII 以外的编码字符。有一个算法可以在这种情况下做 Unicode 与 ASCII 码之间的转换，实现域名的国际化。java.net.IDN 就是实现这个国际化域名转换的新类，IDN 是“国际化域名”的缩写（internationalized domain names）。这个类很简单，主要包括 4 个静态函数，做字符的转换。

<a id='jmx'></a>

## 三、JMX 与系统管理

### 前言

在 Java 程序的运行过程中，对 JVM 和系统的监测一直是 Java 开发人员在开发过程所需要的。一直以来，Java 开发人员必须通过一些底层的 JVM API，比如 JVMPI 和 JVMTI 等，才能监测 Java 程序运行过程中的 JVM 和系统的一系列情况，这种方式一直以来被人所诟病，因为这需要大量的 C 程序和 JNI 调用，开发效率十分低下。于是出现了各种不同的专门做资源管理的程序包。为了解决这个问题，Sun 公司也在其 Java 5 版本中，正式提出了 Java 管理扩展（Java Management Extensions，JMX）用来管理检测 Java 程序（同时 JMX 也在 J2EE 1.4 中被发布）。

JMX 的提出，让 JDK 中开发自检测程序成为可能，也提供了大量轻量级的检测 JVM 和运行中对象 / 线程的方式，从而提高了 Java 语言自己的管理监测能力

JMX 既是 Java 管理系统的一个标准，一个规范，也是一个接口，一个框架。

和其它的资源系统一样，JMX 是管理系统和资源之间的一个接口，它定义了管理系统和资源之间交互的标准。`javax.management.MBeanServer`实现了 Agent 的功能，以标准的方式给出了管理系统访问 JMX 框架的接口。而 `javax.management.MBeans`实现了 SubAgent 的功能，以标准的方式给出了 JMX 框架访问资源的接口。而从类库的层次上看，JMX 包括了核心类库 `java.lang.management`和 `javax.management`包。`java.lang.management`包提供了基本的 VM 监控功能，而 `javax.management`包则向用户提供了扩展功能。

JMX 使用了 Java Bean 模式来传递信息。一般说来，JMX 使用有名的 MBean，其内部包含了数据信息，这些信息可能是：应用程序配置信息、模块信息、系统信息、统计信息等。另外，MBean 也可以设立可读写的属性、直接操作某些函数甚至启动 MBean 可发送的 notification 等。MBean 包括 Standard，MXBean，Dynamic，Model，Open 等几种分类，其中最简单是标准 MBean 和 MXBean，而我们使用得最多的也是这两种。MXBean 主要是 java.lang.management使用较多

### 标准 MBean

标准 MBean 是最简单的一类 MBean，与动态 Bean 不同，它并不实现 javax.management包中的特殊的接口。说它是标准 MBean， 是因为其向外部公开其接口的方法和普通的 Java Bean 相同，是通过 lexical，或者说 coding convention 进行的。下面我们就用一个例子来展现，如何实现一个标准 MBean 来监控某个服务器 ServerImpl 状态的。ServerImpl 代表了用来演示的某个 Server 的实现：

```java
public class ServerImpl { 
   public final long startTime; 
   public ServerImpl() { 
       startTime = System.currentTimeMillis(); 
   } 
}
```

使用一个标准 MBean，ServerMonitor 来监控 ServerImpl

```java
public interface ServerMonitorMBean {
    long getUpTime();
}

public class ServerMonitor implements ServerMonitorMBean { 
   private final ServerImpl target; 
   public ServerMonitor(ServerImpl target){ 
       this.target = target; 
   } 
   public long getUpTime(){ 
       return System.currentTimeMillis() - target.startTime; 
   } 
}
```

MXBean 规定了标准 MBean 也要实现一个接口，所有向外界公开的方法都要在这个接口中声明。否则，管理系统就不能从中获得相应的信息。此外，该接口的名字也有一定的规范：即在标准 MBean 类名之后加上“MBean”后缀，即上面的`ServerMonitorMBean`。对于管理系统来说，这些在 MBean 中公开的方法，最终会被 JMX 转化成属性（Attribute）、监听（Listener）和调用（Invoke）的概念

模拟管理系统：

```java
public class Main {
    private static ObjectName objectName;

    private static MBeanServer mBeanServer;

    public static void main(String[] args) throws Exception {
        init();
        manage();
    }

    private static void init() throws Exception {
        ServerImpl serverImpl = new ServerImpl();
        ServerMonitor serverMonitor = new ServerMonitor(serverImpl);
        mBeanServer = MBeanServerFactory.createMBeanServer();
        objectName = new ObjectName("objectName:id=ServerMonitor1");
        mBeanServer.registerMBean(serverMonitor, objectName);
    }

    private static void manage() throws Exception {
        Long upTime = (Long) mBeanServer.getAttribute(objectName, "UpTime");
        System.out.println(upTime);
    }
}
```

JMX 的核心是 MBServer。Java 已经提供了一个默认实现，可以通过 MBServerFactory.createMBeanServer()获得。每个资源监控者（MBean）一般都会有名称（ObjectName）， 登记在 MBServer 内部的一个 Repository 中。注意，这个 ObjectName 对于每一个 MBServer 必须是唯一的，只能对应于一个 MBean。

### 动态 MBean

但是对于很多已有的 SubAgent 实现，其 Coding Convention 并不符合标准 MBean 的要求。重构所有这些 SubAgent 以符合标准 MBean 标准既费力也不实际。JMX 中给出了动态（Dynamic） MBean 的概念，MBServer 不再依据 Coding Convention 而是直接查询动态 MBean 给出的元数据（meta data）以获得 MBean 的对外接口

```java
public class ServerMonitor implements DynamicMBean {
    private final ServerImpl target;

    private MBeanInfo mBeanInfo;

    public ServerMonitor(ServerImpl target) {
        this.target = target;
    }

    // 实现获取被管理的 ServerImpl 的 upTime 
    public long upTime() {
        return System.currentTimeMillis() - target.startTime;
    }

    //javax.management.MBeanServer 会通过查询 getAttribute("Uptime") 获得 "Uptime" 属性值
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (attribute.equals("UpTime")) {
            return upTime();
        }
        return null;
    }

    // 给出 ServerMonitor 的元信息。  
    public MBeanInfo getMBeanInfo() {
        if (mBeanInfo == null) {
            try {
                Class cls = this.getClass();
                // 用反射获得 "upTime" 属性的读方法
                Method readMethod = cls.getMethod("upTime", new Class[0]);
                // 用反射获得构造方法
                Constructor constructor = cls.getConstructor(new Class[] { ServerImpl.class });
                // 关于 "upTime" 属性的元信息 : 名称为 UpTime，只读属性 ( 没有写方法 )。
                MBeanAttributeInfo upTimeMBeanAttributeInfo = new MBeanAttributeInfo("UpTime",
                        "The time span since server start", readMethod, null);
                // 关于构造函数的元信息
                MBeanConstructorInfo mBeanConstructorInfo = new MBeanConstructorInfo("Constructor for ServerMonitor",
                        constructor);
                //ServerMonitor 的元信息，为了简单起见，在这个例子里，
                // 没有提供 invocation 以及 listener 方面的元信息 
                mBeanInfo = new MBeanInfo(cls.getName(), "Monitor that controls the server",
                        new MBeanAttributeInfo[] { upTimeMBeanAttributeInfo },
                        new MBeanConstructorInfo[] { mBeanConstructorInfo }, null, null);
            } catch (Exception e) {
                throw new Error(e);
            }

        }
        return mBeanInfo;
    }

    public AttributeList getAttributes(String[] arg0) {
        return null;
    }

    public Object invoke(String arg0, Object[] arg1, String[] arg2) throws MBeanException, ReflectionException {
        return null;
    }

    public void setAttribute(Attribute arg0) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        return;
    }

    public AttributeList setAttributes(AttributeList arg0) {
        return null;
    }
}
```

### 其它动态 MBean

另外还有两类 MBean：Open MBean 和 Model MBean。实际上它们也都是动态 MBean。

Open MBean 与其它动态 MBean 的唯一区别在于，前者对其公开接口的参数和返回值有所限制 —— 只能是基本类型或者 javax.management.openmbean包内的 ArrayType、CompositeType、TarbularType 等类型。这主要是考虑到管理系统的分布，很可能远端管理系统甚至 MBServer 层都不具有 MBean 接口中特殊的类

### Model Bean

普通的动态 Bean 通常缺乏一些管理系统所需要的支持：比如持久化 MBean 的状态、日志记录、缓存等等。如果让用户去一一实现这些功能确实是件枯燥无聊的工作。为了减轻用户的负担，JMX 提供商都会提供不同的 ModelBean 实现。其中有一个接口是 Java 规范中规定所有厂商必须实现的：javax.management.modelmbean.RequiredModelBean。通过配置 Descriptor 信息，我们可以定制这个 Model Bean， 指定哪些 MBean 状态需要记入日志、如何记录以及是否缓存某些属性、缓存多久等等。这里，我们以 RequiredModelBean 为例讨论 ModelBean。比如，我们先来看一个例子，首先是 server 端：

```java
public class Server { 
 
    private long startTime; 
    
    public Server() {   } 
    
    public int start(){ 
        startTime = System.currentTimeMillis(); 
        return 0; 
    } 
    
    public long getUpTime(){ 
        return System.currentTimeMillis() - startTime; 
    } 
}
```

然后我们对它的监测如下：

```java
public class Main {
    public static void main(String[] args) throws Exception {
        MBeanServer mBeanServer = MBeanServerFactory.createMBeanServer();
        RequiredModelMBean serverMBean = (RequiredModelMBean) mBeanServer
                .instantiate("javax.management.modelmbean.RequiredModelMBean");

        ObjectName serverMBeanName = new ObjectName("server: id=Server");
        serverMBean.setModelMBeanInfo(getModelMBeanInfoForServer(serverMBeanName));
        Server server = new Server();
        serverMBean.setManagedResource(server, "ObjectReference");

        ObjectInstance registeredServerMBean = mBeanServer.registerMBean((Object) serverMBean, serverMBeanName);

        serverMBean.invoke("start", null, null);

        Thread.sleep(1000);

        System.out.println(serverMBean.getAttribute("upTime"));
        Thread.sleep(5000);
        System.out.println(serverMBean.getAttribute("upTime"));
    }

    private static ModelMBeanInfo getModelMBeanInfoForServer(ObjectName objectName) throws Exception {
        ModelMBeanAttributeInfo[] serverAttributes = new ModelMBeanAttributeInfo[1];
        Descriptor upTime = new DescriptorSupport(new String[] { "name=upTime", "descriptorType=attribute",
                "displayName=Server upTime", "getMethod=getUpTime", });
        serverAttributes[0] = new ModelMBeanAttributeInfo("upTime", "long", "Server upTime", true, false, false, upTime);

        ModelMBeanOperationInfo[] serverOperations = new ModelMBeanOperationInfo[2];

        Descriptor getUpTimeDesc = new DescriptorSupport(new String[] { "name=getUpTime", "descriptorType=operation",
                "class=modelmbean.Server", "role=operation" });

        MBeanParameterInfo[] getUpTimeParms = new MBeanParameterInfo[0];
        serverOperations[0] = new ModelMBeanOperationInfo("getUpTime", "get the up time of the server", getUpTimeParms,
                "java.lang.Long", MBeanOperationInfo.ACTION, getUpTimeDesc);

        Descriptor startDesc = new DescriptorSupport(new String[] { "name=start", "descriptorType=operation",
                "class=modelmbean.Server", "role=operation" });
        MBeanParameterInfo[] startParms = new MBeanParameterInfo[0];
        serverOperations[1] = new ModelMBeanOperationInfo("start", "start(): start server", startParms,
                "java.lang.Integer", MBeanOperationInfo.ACTION, startDesc);

        ModelMBeanInfo serverMMBeanInfo = new ModelMBeanInfoSupport("modelmbean.Server",
                "ModelMBean for managing an Server", serverAttributes, null, serverOperations, null);

        //Default strategy for the MBean. 
        Descriptor serverDescription = new DescriptorSupport(new String[] { ("name=" + objectName),
                "descriptorType=mbean", ("displayName=Server"), "type=modelmbean.Server", "log=T",
                "logFile=serverMX.log", "currencyTimeLimit=10" });
        serverMMBeanInfo.setMBeanDescriptor(serverDescription);
        return serverMMBeanInfo;
    }
}
```

和MBean类似，使用Model MBean的过程也是下面几步：

1. 创建一个MBServer ：mBeanServer
2. 获得管理资源用的MBean ：serverBean
3. 给这个MBean一个ObjectName ：serverMBeanName
4. 将serverBean以serverMBeanName注册到mBeanServer上

不同的是，ModelMBean需要额外两步：

```java
//1
serverMBean.setModelBeanInfo(getModelMBeanInfoForServer(serverMBeanName));
//2
serverMBean.setManagedResource(server, "ObjectReference");
```

第一步用于提供serverMBean的元数据，主要包括两类

- 类似于普通的动态MBean，需要MBean的Attribute、Invocation、Notification的类型/反射信息，诸如返回类型、参数类型以及日志等的策略。
- 关于缓存、持久化日志等的策略。
 
第二步指出了ServerMBean管理的对象，也就是说，从元数据中的到的Method将施加在哪个Object上

Model Bean 与普通动态 Bean 区别在于它的元数据类型 ModelMBeanInfo 扩展了前者的 MBeanInfo，使得 ModelMBeanOperationInfo、ModelMBeanConstructor_Info、ModelMBeanAttributeInfo 和 ModelMBeanNotificationInfo 都有一个额外的元数据：javax.management.Descriptor，它是用来设定 Model Bean 策略的。数据的存储是典型的 "key-value" 键值对。不同的 Model Bean 实现，以及不同的 MBeanFeatureInfo 支持不同的策略特性

<a id='api'></a>

## 四、编译器 API

### 简介

传统的 JSP 技术中，服务器处理 JSP 通常需要进行下面 6 个步骤：

1. 分析 JSP 代码；
2. 生成 Java 代码；
3. 将 Java 代码写入存储器；
4. 启动另外一个进程并运行编译器编译 Java 代码；
5. 将类文件写入存储器；
6. 服务器读入类文件并运行；

JDK 6 提供了在运行时调用编译器的 API，可以同时简化步骤 4 和 5，节约新进程的开销和写入存储器的输出开销，提高系统效率，老版本的编程接口并不是标准 API 的一部分，而是作为 Sun 的专有实现提供的，而新版则带来了标准化的优点。

新 API 的第二个新特性是可以编译抽象文件，理论上是任何形式的对象 —— 只要该对象实现了特定的接口。有了这个特性，上述例子中的步骤 3 也可以省略。整个 JSP 的编译运行在一个进程中完成，同时消除额外的输入输出操作。

第三个新特性是可以收集编译时的诊断信息。作为对前两个新特性的补充，它可以使开发人员轻松的输出必要的编译错误或者是警告信息，从而省去了很多重定向的麻烦

### 运行时编译 Java 文件

在 JDK 6 中，类库通过 javax.tools 包提供了程序运行时调用编译器的 API。这个开发包提供的功能并不仅仅限于编译器。工具还包括 javah、jar、pack200 等，它们都是 JDK 提供的命令行工具。这个开发包希望通过实现一个统一的接口，可以在运行时调用这些工具。在 JDK 6 中，编译器被给予了特别的重视。针对编译器，JDK 设计了两个接口，分别是 JavaCompiler 和 JavaCompiler.CompilationTask。

在运行时调用编译器：

```java
// 指定编译文件名称（该文件必须在 CLASSPATH 中可以找到）
String fullQuanlifiedFileName = "compile" + java.io.File.separator +"Target.java";

// 获得编译器对象
JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
```

通过调用 ToolProvider 的 getSystemJavaCompiler 方法，JDK 提供了将当前平台的编译器映射到内存中的一个对象。这样使用者可以在运行时操纵编译器。JavaCompiler 是一个接口，它继承了 javax.tools.Tool 接口。因此，第三方实现的编译器，只要符合规范就能通过统一的接口调用。同时，tools 开发包希望对所有的工具提供统一的运行时调用接口。

```java
// 编译文件
int result = compiler.run(null, null, null, fileToCompile);
```

获得编译器对象之后，可以调用 Tool.run 方法对源文件进行编译。Run 方法的前三个参数，分别可以用来重定向标准输入、标准输出和标准错误输出，null 值表示使用默认值

完整示例：

```java
public class Target {
    public void doSomething() {
        Date date = new Date(10, 3, 3);
        // 这个构造函数被标记为deprecated, 编译时会
        // 向错误输出输出信息。
        System.out.println("Doing...");
    }
}

public class Compiler {
    public static void main(String[] args) throws Exception {
        String fullQuanlifiedFileName = "compile" + java.io.File.separator + "Target.java";
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        FileOutputStream err = new FileOutputStream("err.txt");

        int compilationResult = compiler.run(null, null, err, fullQuanlifiedFileName);

        if (compilationResult == 0) {
            System.out.println("Done");
        } else {
            System.out.println("Fail");
        }
    }
}
```

首先运行 \bin\javac Compiler.java，然后运行 \bin\java compile.Compiler。屏幕上将输出 Done ，并会在当前目录生成一个 err.txt 文件，文件内容如下：

> compile/Target.java uses or overrides a deprecated API.<br/>
> Recompilewith -Xlint:deprecation for details.

run方法最后一个参数是String...args，是一个变长字符串参数，实际作用是接收传递给javac的参数。假设要编译Target.java文件，并显示编译过程的详细信息。可添加一个命令，如下：

```java
int compilationResult = compiler.run(null, null, err, "-verbose", fullQuanlifiedFileName);
```

### 编译非文本形式的文件

JDK 6 的编译器 API 的另外一个强大之处在于，它可以编译的源文件的形式并不局限于文本文件。JavaCompiler 类依靠文件管理服务可以编译多种形式的源文件。比如直接由内存中的字符串构造的文件，或者是从数据库中取出的文件。这种服务是由 JavaFileManager 类提供的。通常的编译过程分为以下几个步骤：

1. 解析 javac 的参数；
2. 在 source path 和/或 CLASSPATH 中查找源文件或者 jar 包；
3. 处理输入，输出文件；
 
JavaFileManager 类可以起到创建输出文件，读入并缓存输出文件的作用。由于它可以读入并缓存输入文件，这就使得读入各种形式的输入文件成为可能。JDK 提供的命令行工具，处理机制也大致相似，在未来的版本中，其它的工具处理各种形式的源文件也成为可能 

如果要使用 JavaFileManager，就必须构造 CompilationTask。JDK 6 提供了 JavaCompiler.CompilationTask 类来封装一个编译操作:

```java
JavaCompiler.getTask (
    Writer out, 
    JavaFileManager fileManager,
    DiagnosticListener<? super JavaFileObject> diagnosticListener,
    Iterable<String> options,
    Iterable<String> classes,
    Iterable<? extends JavaFileObject> compilationUnits
)
```

构造 CompilationTask 进行编译:

```java
public class Calculator {
    public int multiply(int multiplicand, int multiplier) {
        return multiplicand * multiplier;
    }
}

public class Compiler {
    public static void main(String[] args) throws Exception {
        String fullQuanlifiedFileName = "math" + java.io.File.separator + "Calculator.java";
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjectsFromStrings(Arrays
                .asList(fullQuanlifiedFileName));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, files);

        Boolean result = task.call();
        if (result == true) {
            System.out.println("Succeeded");
        }
    }
}
```

以上是第一步，通过构造一个 CompilationTask 编译了一个 Java 文件。

第二步，生成 Calculator 的一个测试类，而不是手工编写。使用 compiler API，可以将内存中的一段字符串，编译成一个 CLASS 文件。

定制 JavaFileObject 对象:

```java
public class StringObject extends SimpleJavaFileObject {
    private String contents = null;

    public StringObject(String className, String contents) throws Exception {
        super(new URI(className), Kind.SOURCE);
        this.contents = contents;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return contents;
    }
}
```

SimpleJavaFileObject 是 JavaFileObject 的子类，它提供了默认的实现。继承 SimpleJavaObject 之后，只需要实现 getCharContent 方法。接下来，在内存中构造 Calculator 的测试类 CalculatorTest，并将代表该类的字符串放置到 StringObject 中，传递给 JavaCompiler 的 getTask 方法。

编译非文本形式的源文件:

```java
public class AdvancedCompiler {
    public static void main(String[] args) throws Exception {

        // Steps used to compile Calculator
        // Steps used to compile StringObject

        // construct CalculatorTest in memory
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        JavaFileObject file = constructTestor();
        Iterable<? extends JavaFileObject> files = Arrays.asList(file);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, files);

        Boolean result = task.call();
        if (result == true) {
            System.out.println("Succeeded");
        }
    }

    private static SimpleJavaFileObject constructTestor() {
        StringBuilder contents = new StringBuilder("package math;" + "class CalculatorTest {\n"
                + "  public void testMultiply() {\n" + "    Calculator c = new Calculator();\n"
                + "    System.out.println(c.multiply(2, 4));\n" + "  }\n"
                + "  public static void main(String[] args) {\n" + "    CalculatorTest ct = new CalculatorTest();\n"
                + "    ct.testMultiply();\n" + "  }\n" + "}\n");
        StringObject so = null;
        try {
            so = new StringObject("math.CalculatorTest", contents.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return so;
    }
}
```

在内存中构造了 CalculatorTest 类，并且通过 StringObject 的构造函数，将内存中的字符串，转换成了 JavaFileObject 对象。

### 采集编译器的诊断信息

第三个新增加的功能，是收集编译过程中的诊断信息。诊断信息，通常指错误、警告或是编译过程中的详尽输出。JDK 6 通过 Listener 机制，获取这些信息。如果要注册一个 DiagnosticListener，必须使用 CompilationTask 来进行编译

```java
public class Calculator {
    public int multiply(int multiplicand, int multiplier) {
    return multiplicand * multiplier 
      // deliberately omit semicolon, ADiagnosticListener 
      // will take effect
  }
}

public class CompilerWithListener {
    public static void main(String[] args) throws Exception {
        String fullQuanlifiedFileName = "math" + java.io.File.separator + "Calculator.java";
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjectsFromStrings(Arrays
                .asList(fullQuanlifiedFileName));
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, collector, null, null, files);

        Boolean result = task.call();
        List<Diagnostic<? extends JavaFileObject>> diagnostics = collector.getDiagnostics();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics) {
            System.out.println("Line Number->" + d.getLineNumber());
            System.out.println("Message->" + d.getMessage(Locale.ENGLISH));
            System.out.println("Source" + d.getCode());
            System.out.println("\n");
        }

        if (result == true) {
            System.out.println("Succeeded");
        }
    }
}
```

构造一个 DiagnosticCollector 对象，这个对象由 JDK 提供，它实现了 DiagnosticListener 接口。18 行将它注册到 CompilationTask 中去。一个编译过程可能有多个诊断信息。每一个诊断信息，被抽象为一个 Diagnostic。20-26 行，将所有的诊断信息逐个输出。编译并运行 Compiler，得到以下输出：

DiagnosticCollector 收集的编译信息

> Line Number->5 <br/>
> Message->math/Calculator.java:5: ';' expected <br/>
> Source->compiler.err.expected <br/>

实际上，也可以由用户自己定制:

自定义的 DiagnosticListener

```java
class ADiagnosticListener implements DiagnosticListener<JavaFileObject> {
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        System.out.println("Line Number->" + diagnostic.getLineNumber());
        System.out.println("Message->" + diagnostic.getMessage(Locale.ENGLISH));
        System.out.println("Source" + diagnostic.getCode());
        System.out.println("\n");
    }
}
```

<a id='db'></a>

## 五、Java DB 和 JDBC 4.0

### 前言

长久以来，由于大量（甚至几乎所有）的 Java 应用都依赖于数据库，如何使用 Java 语言高效、可靠、简洁地访问数据库一直是程序员们津津乐道的话题。新发布的 Java 6 也在这方面更上层楼，为编程人员提供了许多好用的新特性。其中最显著的，莫过于 Java 6 拥有了一个内嵌的 100% 用 Java 语言编写的数据库系统。并且，Java 6 开始支持 JDBC 4.0 的一系列新功能和属性。这样，Java SE 在对持久数据的访问上就显得更为易用和强大了

### Java DB：Java 6 里的数据库（内嵌模式的 Derby）

除了传统的 bin、jre 等目录，JDK 6 新增了一个名为 db 的目录。这便是 Java 6 的新成员：Java DB。这是一个纯 Java 实现、开源的数据库管理系统（DBMS），源于 Apache 软件基金会（ASF）名下的项目 Derby。

简单示例，在 DBMS 中创建了一个名为 helloDB 的数据库；创建了一张数据表，取名为 hellotable；向表内插入了两条数据；然后，查询数据并将结果打印在控制台上；最后，删除表和数据库，释放资源。

```java
public class HelloJavaDB {
    public static void main(String[] args) {
        try {
            //注册驱动
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            System.out.println("加载内嵌驱动");
            Connection conn = null;
            Properties props = new Properties();
            props.put("user", "user1");
            props.put("password", "user1");
            //创建数据库 和 连接
            conn = DriverManager.getConnection("jdbc:derby:helloBD;create=true", props);
            System.out.println("create and connect to helloDB");
            conn.setAutoCommit(false);

            //创建一张表 插入两条数据
            Statement stat = conn.createStatement();
            stat.execute("create table hellotable(name varchar(40),score int)");
            System.out.println("创建一张表hellotable");
            stat.execute("insert into hellotable values('Jack',85)");
            stat.execute("insert into hellotable values('Rose',95)");
            ResultSet rs = stat.executeQuery("SELECT * FROM hellotable ORDER BY score");
            System.out.println("name\tscore");
            while (rs.next()) {
                StringBuilder builder = new StringBuilder(rs.getString(1));
                builder.append("\t");
                builder.append(rs.getInt(2));
                System.out.println(builder.toString());
            }
            //删除表
            stat.execute("DROP TABLE hellotable");
            System.out.println("删除了hellotable");
            rs.close();
            stat.close();
            System.out.println("关闭resultset和statement");
            conn.commit();
            conn.close();
            System.out.println("关闭连接");

            try {
                //清理数据库 关闭
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (Exception e) {
                System.out.println("已清理数据库 关闭");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("实例结束");
    }
}
```

运行结果：
> 加载内嵌驱动 <br/>
> create and connect to helloDB <br/>
> 创建一张表hellotable <br/>
> name	score <br/>
> Jack	85 <br/>
> Rose	95 <br/>
> 删除了hellotable <br/>
> 关闭resultset和statement <br/>
> 关闭连接 <br/>
> Database shut down normally <br/>
> SimpleApp finished <br/>

关闭所有数据库及 Derby 引擎

```java
DriverManager.getConnection("jdbc:derby:;shutdown=true");
```

关闭一个数据库

```java
DriverManager.getConnection("jdbc:derby:helloDB;shutdown=true ");
```

在使用内嵌模式时，Derby 本身并不会在一个独立的进程中，而是和应用程序一起在同一个 Java 虚拟机（JVM）里运行。因此，Derby 如同应用所使用的其它 jar 文件一样变成了应用的一部分，只有一个 JVM 能够启动数据库。

### 网络服务器模式

网络服务器模式是一种更为传统的客户端/服务器模式。我们需要启动一个 Derby 的网络服务器用于处理客户端的请求，不论这些请求是来自同一个 JVM 实例，还是来自于网络上的另一台机器。同时，客户端使用 DRDA（Distributed Relational Database Architecture）协议连接到服务器端。这是一个由 The Open Group 倡导的数据库交互标准。

Derby网络服务模式架构：

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/Java%E6%96%B0%E7%89%B9%E6%80%A7/Java6/fig003.gif)

网络服务器模式下的 HelloJavaDB：

> 驱动类为 org.apache.derby.jdbc.ClientDriver <br/>
> Derby 网络的客户端的连接格式为：jdbc:derby://server[:port]/databaseName[;attributeKey=value] <br/>
> 连接数据库的协议则变成了 jdbc:derby://localhost:1527/

Derby 中控制网络服务器的类是 org.apache.derby.drda.NetworkServerControl，因此键入以下命令即可:

> java -cp .;"C:\Program Files\Java\jdk1.6.0\db\lib\derby.jar";
"C:\Program Files\Java\jdk1.6.0\db\lib\derbynet.jar" 
org.apache.derby.drda.NetworkServerControl start

相对应的，网络客户端的实现被包含在 derbyclient.jar 中。所以，只需要在 classpath 中加入该 jar 文件，修改后的客户端就可以顺利地读取数据了。再一次尝试着使用两个命令行窗口去连接数据库，就能够得到正确的结果了。如果不再需要服务器，那么使用 NetworkServerControl 的 shutdown 参数就能够关闭服务器

### JDBC 4.0：新功能，新 API

**自动加载驱动**

在 JDBC 4.0 之前，编写 JDBC 程序都需要加上以下这句:

```
Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
```

Java.sql.DriverManager 的内部实现机制决定了这样代码的出现。只有先通过 Class.forName 找到特定驱动的 class 文件，DriverManager.getConnection 方法才能顺利地获得 Java 应用和数据库的连接。从 Java 6 开始，应用程序不再需要显式地加载驱动程序了，DriverManager 开始能够自动地承担这项任务。

JDBC 4.0 的规范规定，所有 JDBC 4.0 的驱动 jar 文件必须包含一个 java.sql.Driver，它位于 jar 文件的 META-INF/services 目录下。这个文件里每一行便描述了一个对应的驱动类。DriverManager 就可以从当前在 CLASSPATH 中的驱动文件中找到，它应该去加载哪些类。如果开发人员想使得自己的驱动也能够被 DriverManager 找到，只需要将对应的 jar 文件加入到 CLASSPATH 中就可以了。

**RowId**

熟悉 DB2、Oracle 等大型 DBMS 的人一定不会对 ROWID 这个概念陌生：它是数据表中一个“隐藏”的列，是每一行独一无二的标识，表明这一行的物理或者逻辑位置。Java 6 中新增了 java.sql.RowId 的数据类型，允许 JDBC 程序能够访问 SQL 中的 ROWID 类型。但是不是所有的DBMS都支持RowId，并且不同的RowId有不同的生存命周期。可以使用DatabaseMetaData.getRowIdLifetime来判断类型的生命周期。

```java
DatabaseMetaData meta = conn.getMetaData();
System.out.println(meta.getRowIdLifetime());
```

Java 6 的 API 规范中，java.sql.RowIdLifetime 规定了 5 种不同的生命周期：

- ROWID_UNSUPPORTED 此数据源不支持 ROWID 类型
- ROWID_VALID_FOREVER 来自此数据源的 RowId 的生存期实际上没有限制
- ROWID_VALID_OTHER 来自此数据源的 RowId 的生存期是不确定的；但不是 ROWID_VALID_TRANSACTION、ROWID_VALID_SESSION 或 ROWID_VALID_FOREVER 之一
- ROWID_VALID_SESSION 来自此数据源的 RowId 至少在包含它的会话期间有效
- ROWID_VALID_TRANSACTION 来自此数据源的 RowId 的生存期至少在包含它的事务期间有效

获得/设置 RowId 对象：

```java
// Initialize a PreparedStatement
PreparedStatement pstmt = connection.prepareStatement(
    "SELECT rowid, name, score FROM hellotable WHERE rowid = ?");
// Bind rowid into prepared statement. 
pstmt.setRowId(1, rowid);
// Execute the statement
ResultSet rset = pstmt.executeQuery(); 
// List the records
while(rs.next()) {
    RowId id = rs.getRowId(1); // get the immutable rowid object
    String name = rs.getString(2);
    int score = rs.getInt(3);
}
```

鉴于不同 DBMS 的不同实现，RowID 对象通常在不同的数据源（datasource）之间并不是可移植的。因此 JDBC 4.0 的 API 规范并不建议从连接 A 取出一个 RowID 对象，将它用在连接 B 中，以避免不同系统的差异而带来的难以解释的错误。而至于像 Derby 这样不支持 RowId 的 DBMS，程序将直接在 setRowId 方法处抛出 SQLFeatureNotSupportedException。

**SQLXML**

SQL：2003 标准引入了 SQL/XML，作为 SQL 标准的扩展。SQL/XML 定义了 SQL 语言怎样和 XML 交互：如何创建 XML 数据；如何在 SQL 语句中嵌入 XQuery 表达式等等。作为 JDBC 4.0 的一部分，Java 6 增加了 java.sql.SQLXML 的类型。JDBC 应用程序可以利用该类型初始化、读取、存储 XML 数据。java.sql.Connection.createSQLXML 方法就可以创建一个空白的 SQLXML 对象。当获得这个对象之后，便可以利用 setString、setBinaryStream、setCharacterStream 或者 setResult 等方法来初始化所表示的 XML 数据。

利用 setCharacterStream 方法来初始化 SQLXML 对象：

```java
SQLXML xml = con.createSQLXML();
Writer writer = xml.setCharacterStream();
BufferedReader reader = new BufferedReader(new FileReader("test.xml"));
String line= null;
while((line = reader.readLine() != null) {
      writer.write(line);
}
// 释放资源
xml.free();
```

**SQLExcpetion 的增强**

Java 6 的设计人员对以 java.sql.SQLException 为根的异常体系作了大幅度的改进，新实现了 Iterable<Throwable> 接口，这样简洁地遍历了每一个 SQLException 和它潜在的原因（cause）

```java
try {
    //...
} catch (Throwable e) {
   if (e instanceof SQLException) {
       for(Throwable ex : (SQLException)e ){
            System.err.println(ex.toString());
        }
    }
}
```

除去原有的 SQLException 的子类，Java 6 中新增的异常类被分为 3 种：SQLReoverableException、SQLNonTransientException、SQLTransientException。在 SQLNonTransientException 和 SQLTransientException 之下还有若干子类，详细地区分了 JDBC 程序中可能出现的各种错误情况。大多数子类都会有对应的标准 SQLState 值，很好地将 SQL 标准和 Java 6 类库结合在一起。

<a id='script'></a>

## 六、对脚本语言的支持

### 概述

Java 6 引入了对 Java Specification Request（JSR）223 的支持，JSR 223 旨在定义一个统一的规范，使得 Java 应用程序可以通过一套固定的接口与各种脚本引擎交互，从而达到在 Java 平台上调用各种脚本语言的目的。javax.script 包定义了这些接口，即 Java 脚本编程 API。

> 脚本引擎就是指脚本的运行环境，它能能够把运行其上的解释性语言转换为更底层的汇编语言，没有脚本引擎，脚本就无法被运行。

在 javax.script 包中定义的实现类并不多，主要是一些接口和对应的抽象类，至于对如何解析运行具体的脚本语言，还需要由第三方提供实现。虽然这些脚本引擎的实现各不相同，但是对于 Java 脚本 API 的使用者来说，这些具体的实现被很好的隔离隐藏了。

- 获取脚本程序输入，通过脚本引擎运行脚本并返回运行结果，这是最核心的接口。
- 发现脚本引擎，查询脚本引擎信息。
- 通过脚本引擎的运行上下文在脚本和 Java 平台间交换数据。
- 通过 Java 应用程序调用脚本函数。

简单例子 打印Hello World：

```java
public class HelloWorld {
    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval("print ('Hello World')");
    }
}
```

### 使用脚本引擎运行脚本

Java 脚本 API 通过脚本引擎来运行脚本，整个包的目的就在于统一 Java 平台与各种脚本引擎的交互方式，制定一个标准，Java 应用程序依照这种标准就能自由的调用各种脚本引擎，而脚本引擎按照这种标准实现，就能被 Java 平台支持。每一个脚本引擎就是一个脚本解释器，负责运行脚本，获取运行结果。ScriptEngine 接口是脚本引擎在 Java 平台上的抽象，Java 应用程序通过这个接口调用脚本引擎运行脚本程序，并将运行结果返回给虚拟机。

ScriptEngine 还提供了以一个 java.io.Reader 作为输入参数的 eval 函数。脚本程序实质上是一些可以用脚本引擎执行的字节流，通过一个 Reader 对象，eval 函数就能从不同的数据源中读取字节流来运行，这个数据源可以来自内存、文件，甚至直接来自网络。这样 Java 应用程序就能直接利用项目原有的脚本资源，无需以 Java 语言对其进行重写，达到脚本程序与 Java 平台无缝集成的目的。

运行脚本：

```java
public class RunScript {
 
    public static void main(String[] args) throws Exception {
        String script = args[0];
        String file = args[1];
        
        FileReader scriptReader = new FileReader(new File(file));
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(script);
        engine.eval(scriptReader);
    }
}
```

从命令行分别获取脚本名称和脚本文件名，程序通过脚本名称创建对应的脚本引擎实例，通过脚本名称指定的脚本文件名读入脚本程序运行。运行下面这个命令，就能在 Java 平台上运行所有的 JavaScript 脚本。

> java RunScript javascript run.js

EngineScript 接口分别针对 String 输入和 Reader 输入提供了三个不同形态的 eval 函数，用于运行脚本：

函数 | 描述
--- | ---
Object  eval(Reader reader) | 从一个 Reader 读取脚本程序并运行
Object eval(Reader reader, Bindings n) | 以 n 作为脚本级别的绑定，从一个 Reader 读取脚本程序并运行
Object eval(Reader reader, ScriptContext context) | 在 context 指定的上下文环境下，从一个 Reader 读取脚本程序并运行
Object eval(String script) | 运行字符串表示的脚本
Object eval(String script, Bindings n) | 以 n 作为脚本级别的绑定，运行字符串表示的脚本
Object eval(String script, ScriptContext context) | 在 context 指定的上下文环境下，运行字符串表示的脚本

Java 脚本 API 还为 ScriptEngine 接口提供了一个抽象类 —— AbstractScriptEngine，这个类提供了其中四个 eval 函数的默认实现，它们分别通过调用 eval(Reader,ScriptContext) 或 eval(String, ScriptContext) 来实现。这样脚本引擎提供者，只需继承这个抽象类并提供这两个函数实现即可。AbstractScriptEngine 有一个保护域 context 用于保存默认上下文的引用，SimpleScriptContext 类被作为 AbstractScriptEngine 的默认上下文。

### 发现和创建脚本引擎

JSR 223 中引入 ScriptEngineManager 类的意义就在于，将 ScriptEngine 的寻找和创建任务委托给 ScriptEngineManager 实例处理，达到对 API 使用者隐藏这个过程的目的，使 Java 应用程序在无需重新编译的情况下，支持脚本引擎的动态替换

- ScriptEngineManager 类：自动寻找 ScriptEngineFactory 接口的实现类
- ScriptEngineFactory 接口：创建合适的脚本引擎实例

ScriptEngineManager 类本身并不知道如何创建一个具体的脚本引擎实例，它会依照 Jar 规约中定义的服务发现机制，查找并创建一个合适的 ScriptEngineFactory 实例，并通过这个工厂类来创建返回实际的脚本引擎。首先，ScriptEngineManager 实例会在当前 classpath 中搜索所有可见的 Jar 包；然后，它会查看每个 Jar 包中的 META -INF/services/ 目录下的是否包含 javax.script.ScriptEngineFactory 文件，脚本引擎的开发者会提供在 Jar 包中包含一个 ScriptEngineFactory 接口的实现类，这个文件内容即是这个实现类的完整名字；ScriptEngineManager 会根据这个类名，创建一个 ScriptEngineFactory 接口的实例；最后，通过这个工厂类来实例化需要的脚本引擎，返回给用户。

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/Java%E6%96%B0%E7%89%B9%E6%80%A7/Java6/fig0021.jpg)

ScriptEngineFactory 接口的实现类被用来描述和实例化 ScriptEngine 接口，每一个实现 ScriptEngine 接口的类会有一个对应的工厂类来描述其元数据（meta data），ScriptEngineFactory 接口定义了许多函数供 ScriptEngineManager 查询这些元数据，ScriptEngineManager 会根据这些元数据查找需要的脚本引擎

函数 | 描述
--- | ---
String getEngineName() | 返回脚本引擎的全称
String getEngineVersion() | 返回脚本引擎的版本信息
String getLanguageName() | 返回脚本引擎所支持的脚本语言的名称
String getLanguageVersion() | 返回脚本引擎所支持的脚本语言的版本信息
List<String> getExtensions() | 返回一个脚本文件扩展名组成的 List，当前脚本引擎支持解析这些扩展名对应的脚本文件
List<String> getMimeTypes() | 返回一个与当前引擎关联的所有 mimetype 组成的 List
List<String> getNames() | 返回一个当前引擎所有名称的 List，ScriptEngineManager 可以根据这些名字确定对应的脚本引擎

### 脚本引擎的运行上下文

如果仅仅是通过脚本引擎运行脚本的话，还无法体现出 Java 脚本 API 的优点，在 JSR 223 中，还为所有的脚本引擎定义了一个简洁的执行环境。我们都知道，在 Linux 操作系统中可以维护许多环境变量比如 classpath、path 等，不同的 shell 在运行时可以直接使用这些环境变量，它们构成了 shell 脚本的执行环境。在 javax.script 支持的每个脚本引擎也有各自对应的执行的环境，脚本引擎可以共享同样的环境，也可以有各自不同的上下文。通过脚本运行时的上下文，脚本程序就能自由的和 Java 平台交互，并充分利用已有的众多 Java API，真正的站在“巨人”的肩膀上。javax.script.ScriptContext 接口和 javax.script.Bindings 接口定义了脚本引擎的上下文。

**Bindings 接口：**

继承自 Map，定义了对这些“键-值”对的查询、添加、删除等 Map 典型操作。Bingdings 接口实际上是一个存放数据的容器，它的实现类会维护许多“键-值”对，它们都通过字符串表示。Java 应用程序和脚本程序通过这些“键-值”对交换数据。只要脚本引擎支持，用户还能直接在 Bindings 中放置 Java 对象，脚本引擎通过 Bindings 不仅可以存取对象的属性，还能调用 Java 对象的方法，这种双向自由的沟通使得二者真正的结合在了一起。

**ScriptContext 接口：**

将 Bindings 和 ScriptEngine 联系在了一起，每一个 ScriptEngine 都有一个对应的 ScriptContext，前面提到过通过 ScriptEnginFactory 创建脚本引擎除了达到隐藏实现的目的外，还负责为脚本引擎设置合适的上下文。ScriptEngine 通过 ScriptContext 实例就能从其内部的 Bindings 中获得需要的属性值。

ScriptContext 接口默认包含了两个级别的 Bindings 实例的引用，分别是全局级别和引擎级别，可以通过 **GLOBAL_SCOPE** 和 **ENGINE_SCOPE** 这两个类常量来界定区分这两个 Bindings 实例

GLOBAL_SCOPE 从创建它的 ScriptEngineManager 获得。全局级别指的是 Bindings 里的属性都是“全局变量”，只要是同一个 ScriptEngineMananger 返回的脚本引擎都可以共享这些属性；

ENGINE_SCOPE 级别的 Bindings 里的属性则是“局部变量”，它们只对同一个引擎实例可见，从而能为不同的引擎设置独特的环境，通过同一个脚本引擎运行的脚本运行时能共享这些属性。

ScriptContext 存取属性函数：

函数 | 描述
--- | ---
Object removeAttribute(String name, int scope) | 从指定的范围里删除一个属性
void setAttribute(String name, Object value, int scope) | 在指定的范围里设置一个属性的值
Object getAttribute(String name) | 从上下文的所有范围内获取优先级最高的属性的值
Object getAttribute(String name, int scope) | 从指定的范围里获取属性值

Bindings 在 Java 脚本 API 中，上下文属性的作用域：

```java
public class ScopeTest {
    public static void main(String[] args) throws Exception {
        String script=" println(greeting) ";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
         
        //Attribute from ScriptEngineManager
        manager.put("greeting", "Hello from ScriptEngineManager");
        engine.eval(script);
 
        //Attribute from ScriptEngine
        engine.put("greeting", "Hello from ScriptEngine");
        engine.eval(script);
 
        //Attribute from eval method
        ScriptContext context = new SimpleScriptContext();
        context.setAttribute("greeting", "Hello from eval method", ScriptContext.ENGINE_SCOPE);
        engine.eval(script,context);
    }
}
//输出
//Hello from ScriptEngineManager
//Hello from ScriptEngine
//Hello from eval method
```

ScriptContext 输入输出重定向

函数 | 描述
--- | ---
void setErrorWriter(Writer writer) | 重定向错误输出，默认是标准错误输出
void setReader(Reader reader) | 重定向输入，默认是标准输入
void setWriter(Writer writer) | 重定向输出，默认是标准输出
Writer getErrorWriter() | 获取当前错误输出字节流
Reader getReader() | 获取当前输入流
Writer getWriter() | 获取当前输出流

示例：

```java
public class Redirectory {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
 
        PipedReader pr = new PipedReader();
        PipedWriter pw = new PipedWriter(pr);
        PrintWriter writer = new PrintWriter(pw);
        engine.getContext().setWriter(writer);
 
        String script = "println('Hello from JavaScript')";
        engine.eval(script);
         
        BufferedReader br =new BufferedReader(pr);
        System.out.println(br.readLine());
    }
}
//输出
//Hello from JavaScript
```

### 脚本引擎可选的接口

在 Java 脚本 API 中还有两个脚本引擎可以选择是否实现的接口，这个两个接口不是强制要求实现的，即并非所有的脚本引擎都能支持这两个函数，不过 Java 6 自带的 JavaScript 引擎支持这两个接口

- Invocable 接口：允许 Java 平台调用脚本程序中的函数或方法。
- Compilable 接口：允许 Java 平台编译脚本程序，供多次调用。

**Invocable 接口**

有时候，用户可能并不需要运行已有的整个脚本程序，而仅仅需要调用其中的一个过程，或者其中某个对象的方法，这个时候 Invocable 接口就能发挥作用。它提供了两个函数 invokeFunction 和 invokeMethod，分别允许 Java 应用程序直接调用脚本中的一个全局性的过程以及对象中的方法，调用后者时，除了指定函数名字和参数外，还需要传入要调用的对象引用，当然这需要脚本引擎的支持。不仅如此，Invocable 接口还允许 Java 应用程序从这些函数中直接返回一个接口，通过这个接口实例来调用脚本中的函数或方法，从而我们可以从脚本中动态的生成 Java 应用中需要的接口对象。

```java
public class CompilableTest {
    public static void main(String[] args) throws ScriptException,
            NoSuchMethodException {
        String script = " function greeting(message){println (message);}";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        engine.eval(script);
 
        if (engine instanceof Invocable) {
            Invocable invocable = (Invocable) engine;
            invocable.invokeFunction("greeting", "hello world");
 
            // It may through NoSuchMethodException 
            try {
                invocable.invokeFunction("nogreeing");
            } catch (NoSuchMethodException e) {
                // expected
            }
        }
    }
}
//输出
//hello world
```

**Compilable 接口**

Java 脚本 API 还为这个中间形式提供了一个专门的类，每次调用 Compilable 接口的编译函数都会返回一个 CompiledScript 实例。CompiledScript 类被用来保存编译的结果，从而能重复调用脚本而没有重复解释的开销，实际效率提高的多少取决于中间形式的彻底程度，其中间形式越接近低级语言，提高的效率就越高。每一个 CompiledScript 实例对应于一个脚本引擎实例，一个脚本引擎实例可以含有多个 CompiledScript，调用 CompiledScript 的 eval 函数会传递给这个关联的 ScriptEngine 的 eval 函数。关于 CompiledScript 类需要注意的是，它运行时对与之对应的 ScriptEngine 状态的改变可能会传递给下一次调用，造成运行结果的不一致。

```java
public class CompilableTest {
    public static void main(String[] args) throws ScriptException {
        String script = " println (greeting); greeting= 'Good Afternoon!' ";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        engine.put("greeting", "Good Morning!");
         
        if (engine instanceof Compilable) {
            Compilable compilable = (Compilable) engine;
            CompiledScript compiledScript = compilable.compile(script);
            compiledScript.eval();
            compiledScript.eval();
        }
    }
}
//输出
//Good Morning!
//Good Afternoon!
```

### jrunscript 工具

Java 6 还为运行脚本添加了一个专门的工具 —— jrunscript。jrunscript 支持两种运行方式：

一种是交互式，即边读取边解析运行，这种方式使得用户可以方便调试脚本程序，马上获取预期结果；

还有一种就是批处理式，即读取并运行整个脚本文件。用户可以把它想象成一个万能脚本解释器，即它可以运行任意脚本程序，而且它还是跨平台的，当然所有这一切都有一个前提，那就是必须告诉它相应的脚本引擎的位置。

<a id='xml'></a>

## 七、XML API 与 Web 服务

### 概述

Java 6 做为一个开发平台，针对不同的应用开发需求，提供了各种各样的技术框架。

XML 处理框架是 JDK 6 的重要组成部分之一。它为应用程序开发人员提供了一个统一的 XML 处理 API。

这种框架结构有两个作用：

一方面，开发人员透过这些框架，可以透明的替换不同厂商提供的 XML 处理服务；

另一方面，服务提供商可以透过这些框架，将自己的产品插入到 JDK 中。这种框架一般被称为 Service Provider 机制。Java 6 的 XML 处理功能分为两个部分：XML 处理（JAXP）和 XML 绑定（JAXB）。在 XML 处理框架之上，Java 6 结合了注释（Annotation）技术，提供了强大的针对 Web 服务的支持。

### Service Provider 机制

> 服务（service）是指那些成为事实上标准的接口，服务提供者（service provider）则提供了这个接口的具体实现。不同的提供者会遵循同样的接口提供实现，客户可以自由选择不同的实现。使用配置文件指定，然后在运行时载入具体实现

Service Provider 框架都提供了 3 个主要个组件：面向开发者的 Application接口，面向服务提供商的 Service Provider接口和真正的服务提供者

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/Java%E6%96%B0%E7%89%B9%E6%80%A7/Java6/fig001.jpg)

优点：

1.提供了供应商的中立性，应用代码与服务提供商完全独立，互不依赖。应用程序开发者针对Application接口进行开发。这个接口将不同提供商的接口差异性屏蔽掉了。无论使用哪个厂商的服务，应用程序都是针对一个稳定、统一的接口开发，业务逻辑和第三方组件之间有很强的独立性。

```java
SAXParserFactory factory = SAXParserFactory.newInstance(); 
System.out.println(factory.getClass()); 
 
// Parse the input 
SAXParser saxParser = factory.newSAXParser(); 
System.out.println(saxParser.getClass()); 
 
Output: class org.apache.xerces.jaxp.SAXParserFactoryImpl 
Output: class org.apache.xerces.jaxp.SAXParserImpl
```

本例中 saxParser 的类型被声明为 SAXParser，但实际类型为 org.apache.xerces.jaxp.SAXParserImpl。实际类型是由 SAXParserFactory的静态方法 newInstance查找配置文件，并实例化得到的

2.提供了扩展性，更多的服务可以加入开发平台；为了便于不同的开发商开发各自的产品，Java平台同时为服务提供商设计了统一的接口。只要提供者满足这些接口定义（比如继承某个接口，或者扩展抽象类），服务提供者就能被添加到 Java平台中来。

3.兼顾了灵活性和效率。通过这种方式，一方面组件提供者和应用开发者开发时绑定到一个约定的接口上，另一方面载入具体哪个组件则是在运行时动态决定的。不同于 Web 服务等技术的完全动态绑定（通过运行时解析 WSDL 文件来决定调用的类型），也不是完全的编码时绑定，这种折衷的方式提供了松耦合、高扩展性，同时也保证了可接受的效率。

### XML 框架介绍

Java 6 平台提供的 XML 处理主要包括两个功能：XML 处理（JAXP，Java Architecture XML Processing）和 XML 绑定（JAXB，Java Architecture XML Binding）。

- JAXP 包括:
    - SAX 框架 —— 遍历元素，做出处理
    - DOM 框架 —— 构造 XML 文件的树形表示
    - StAX 框架 —— 拖拽方式的解析
    - XSLT 框架 —— 将 XML 数据转换成其他格式
- JAXB 则是负责将 XML 文件和 Java 对象绑定，在新版 JDK 中，被大量的使用在 Web 服务技术中。

**SAX 框架（Simple API for XML）**

SAX 全称 Simple API for XML，该框架使用了事件处理机制来处理 XML 文件

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/Java%E6%96%B0%E7%89%B9%E6%80%A7/Java6/fig003.jpg)

SAXParser 将 XML 文件当作流读入。当 parser 遇到 Element_A，就会产生一个事件，然后将该事件发送给处理类。SAX 框架的一大特点是对于节点的处理是上下文无关的。比如 图 3中 SAXParser，允许注册一个处理类，这个处理类对于所有节点并不加以区分，对他们的处理过程都是一致的。一般包括 startElement 和 endElement 等动作。

```
    CLASS Listener < DefaultListener
        PROCEDURE StartElement( … ) 
           IF ( node->Obj.name == ‘ Node ’ ) 
                // Do some calculation 
           FI 
        END 
    END 
SAXParser->SetListener(new Listener)
```

使用 SAX 框架，对于 Node 节点的处理并不会根据其前驱或者后缀节点的不同而有所区别。一旦发现节点名称是 Node，则进行预定的处理。这个框架本身并不支持对节点进行上下文相关的处理，除非开发者另外维护一些数据结构来记录上下文状态。正是由于 SAX 框架不需要记录的状态信息，所以运行时，SAX 框架占用的内存（footprint）比较小，解析的速度也比较快。

**DOM 框架（Document Object Model）**

DOM 框架的全称是 Document Object Model。这个框架会建立一个对象模型。针对每个节点，以及节点之间的关系在内存中生成一个树形结构。这个特点与 SAX 框架截然相反。需要注意的是，DOM 框架提供的对象树模型与我们通常理解的 XML 文件结构树模型是有一定的区别的。

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/Java%E6%96%B0%E7%89%B9%E6%80%A7/Java6/fig004.jpg)

Element_B 的 XML 结构:

```xml
<Element_B>This is start of Element_B 
   <Node> … </Node> 
   This is end of Element_B 
</Element_B>
```

Element_B包含子元素 Node，而两句话”This is start of Element_B”与”This is end of Element_B”是 Element_B节点的内容。而实际上，当针对 Element_B调用 Element.getContent，得到的是 Element_B这个名字本身，两句文本同 Node一样，也是作为子节点的。可以这样认为，DOM 的对象模型，在内存中模拟的是 XML 文件的物理存储结构，而不是节点间的逻辑关系。DOM 中结点的类型也是通过 getContent返回的节点名字符串区别的。

```
name = Element.getContent 
SWITCH name 
CASE Element_A: 
    // Do something 
    BREAK 
CASE Element_B: 
    // Do something 
    BREAK 
DEFAULT: 
END
```

DOM 的目标是一个编程语言无关的，用来处理大段复杂 XML 文件的框架。D

OM 框架一个显著的特征是善于处理节点与文本混合的 XML 文件（Mixed-Content Model）。这种设计使得 XML 形成树的过程是一个直接映射，不需要进行概念上的转换，也就节省掉很多的处理细节，一定程度上提高了效率。

**StAX 框架（Streaming API for XML）**

SAX 框架的缺点是不能记录正在处理元素的上下文。但是优点是运行时占内存空间比较小，效率高。DOM 框架由于在处理 XML 时需要为其构造一棵树，所以特点正好相反。StAX 框架出现于 Java 6 中，它的设计目标就是要结合 SAX 框架和 DOM 框架的优点。既要求运行时效率，也要求保持元素的上下文状态。

```java
public class StAXTest { 
 
    public static void main(String[] args) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream input = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<work-contact-info>" + "<Location>Shanghai-shuion-333</Location>" + "<Postal>200020</Postal>"
                + "<Tel><fix>63262299</fix><mobile>" + "1581344454</mobile></Tel>"
                + "<Appellation>Mr. Wang</Appellation>" + "</work-contact-info>").getBytes());
        try {
            XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(input);
            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    System.out.println(startElement.getName().toString());
                }

                if (event.isCharacters()) {
                    Characters text = event.asCharacters();
                    if (!text.isWhiteSpace()) {
                        System.out.println("\t" + text.getData());
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
//work-contact-info
//Location
//	Shanghai-shuion-333
//Postal
//	200020
//Tel
//fix
//	63262299
//mobile
//	1581344454
//Appellation
//	Mr. Wang
```

StAX 框架和 SAX 框架具有相似的地方。StAX 有 Event.isStartElement方法，SAX 有 DefaultHandler.startElement方法。StAX 有 Event.isCharacter方法，SAX 有 DefaultHandler.character方法。实际上这两个框架处理 XML 文件的时候使用了相似的模型——将 XML 文件作为元素组成的流，而不同于 DOM 的树模型。解析 XML 文件时，应用程序调用 XMLEventReader的 nextEvent方法解析下一个元素（或者是解析同一个元素，根据解析的不同阶段，产生不同元素），StAX 就会通过 XMLEventReader产生一个事件。比如针对同一个元素，可能会产生 StartElement和 EndElement事件。形象的说 XMLEventReader就像是一根绳子，拽一下，解析一个元素，产生一个事件。于是这种技术也被称为”Pull Parser”技术。StAX 在处理 XML 文件时，产生的所有事件是通过一个 Iterator（XMLEventReader继承了 Iterator）返回的。应用程序通过这个 Iterator能知道某个解析事件的前后分别是什么。这类信息就是一个元素的上下文信息。

**XSLT 数据转换框架（The Extensible Stylesheet Language Transformations APIs）**

一般来说 XML 文件格式被认为是一种很好的数据交换格式。于是 Java SE 6 SDK 基于以上介绍的三种 XML 处理机制，提供了一个 XML 转换框架。XSLT 框架负责进行转换 —— 包括将 XML 文件转换成其他形式如 HTML，和将其他形式的文件转换成 XML 文件。更进一步说，这个框架可以接受 DOM 作为其输入和输出；可以接受 SAX 解析器作为输入或者产生 SAX 事件作为输出；可以接受 I/O Stream 作为输入和输出；当然也支持用户自定义形式的输入和输出。

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/Java%E6%96%B0%E7%89%B9%E6%80%A7/Java6/fig005.jpg)

转换框架的输入输出对象的类型并不要求是一一对应的。比如，使用 DOMSource做为输入，可以使用 StreamResult作为输出。

```
// Construct input 
factory = XMLParserDocumentFactory->NEW 
parser = factory->NewParser 
document = parser->Parse(File) 
 
// Wrap input/output 
source = Source->NEW( document ) 
sink = Result->NEW 
 
// Construct transformer 
tFactory = TransformerFactory->NEW 
transformer = tFactory->NewTransformer 
 
// Transform 
transformer->Transfer( source, sink)
```

通过这个过程的转化，一个 javax.xml.transform.Source可以转化成为类型 javax.xml.transform.Result。

粗略度量 SAX、StAX、DOM 三个框架解析同一个 XML 文件的运行效率的代码：

```java
public class StAXTest { 
 
   public static void main(String[] args) { 
       final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
           "<work-contact-info>" + 
           "<Location>Shanghai-shuion-333</Location>" + 
           "<Postal>200020</Postal>" + 
           "<Tel><fix>63262299</fix>" + 
           "<mobile>1581344454</mobile></Tel>" + 
           "<Appellation>Mr. Wang</Appellation>" + 
           "</work-contact-info>"; 
       for (int i = 0; i < 10000; i++) { 
           StAX(xml); 
       } 
 
       for (int i = 0; i < 10000; i++) { 
           SAX(xml); 
       } 
 
       for (int i = 0; i < 10000; i++) { 
           DOM(xml); 
       } 
 
       long current = System.currentTimeMillis(); 
       for (int i = 0; i < 10000; i++) { 
           StAX(xml); 
       } 
       current = System.currentTimeMillis() - current; 
       System.out.println(current); 
 
       current = System.currentTimeMillis(); 
       for (int i = 0; i < 10000; i++) { 
            SAX(xml); 
       } 
       current = System.currentTimeMillis() - current; 
       System.out.println(current); 
 
       current = System.currentTimeMillis(); 
       for (int i = 0; i < 10000; i++) { 
            DOM(xml); 
       } 
       current = System.currentTimeMillis() - current; 
       System.out.println(current); 
   } 
 
   private static void StAX(final String xml) { 
       XMLInputFactory inputFactory = XMLInputFactory.newInstance(); 
       InputStream input; 
       try { 
           input = new ByteArrayInputStream(xml.getBytes()); 
            XMLEventReader xmlEventReader = inputFactory 
               .createXMLEventReader(input); 
           while (xmlEventReader.hasNext()) { 
               XMLEvent event = xmlEventReader.nextEvent(); 
 
               if (event.isStartElement()) { 
                   StartElement startElement = event.asStartElement(); 
               } 
 
               if (event.isCharacters()) { 
                   Characters text = event.asCharacters(); 
                   if (!text.isWhiteSpace()) { 
                   } 
               } 
           } 
       } catch (XMLStreamException e) { 
           e.printStackTrace(); 
       } 
   } 
 
   private static void SAX(final String xml) { 
       SAXParserFactory f = SAXParserFactory.newInstance(); 
       InputStream input; 
       try { 
           SAXParser p = f.newSAXParser(); 
           input = new ByteArrayInputStream(xml.getBytes()); 
           p.parse(input, new DefaultHandler()); 
 
       } catch (Exception e) { 
           e.printStackTrace(); 
       } 
   } 
 
   private static void DOM(final String xml) { 
       DocumentBuilderFactory f = DocumentBuilderFactory.newInstance(); 
       InputStream input; 
       try { 
           DocumentBuilder p = f.newDocumentBuilder(); 
           input = new ByteArrayInputStream(xml.getBytes()); 
           p.parse(input); 
 
       } catch (Exception e) { 
           e.printStackTrace(); 
       } 
   } 
}
//输出
//2734 
//4953 
//6516
```

可以看出解析速度按 SAX -> StAX -> DOM 依次变慢。这组数据从一个侧面反映了这三种技术的特性。SAX 处理小的，简单的 XML 文件更高效。基于三种 XML 解析技术，Java 6 SDK 又提供了数据格式转换框架 —— XSLT。同时 XSLT 技术和其他很多的 JDK 框架一样，是一个开放框架。它提供了一些抽象类和接口，让应用程序可以根据需求，开发出不同的 XML 数据处理和转换工具。

### Web 服务

基于 XML 的数据通常被作为 Web 服务之间互相调用的标准的数据传输文件格式。Java 6 SDK 中基于 XML 的解析技术，也提供了 Web 服务的 API 支持。和较早的 JDK 5 相比，新版本的 JDK Web 服务功能更改了名称 —— 从 JAX-RPC 变成 JAX-WS。JDK 5 只支持基于 remote-procedure-call 的 Web 服务，JDK 6 在此基础上，还支持基于 SOAP message 的 Web 服务实现。

1.一个 Web service ‘ Hello ’服务：

```java
@WebService 
public class Hello { 
   @WebMethod 
   public String hello(String name) { 
       return "Hello, " + name + "\n"; 
   } 
 
   public static void main(String[] args) { 
       // create and publish an endpoint 
       Hello hello = new Hello(); 
       Endpoint endpoint = Endpoint.publish("http://localhost:8080/hello", hello); 
   } 
}
```

2.使用apt编译Hello.java，产生辅助文件

> apt -d sample example/Calculator.java

运行完这条命令之后，example 目录下面多出了一个 jaxws 子目录。Apt 工具在该目录里生成了发布 Hello Web service 所必需的两个辅助文件。

3.发布 Hello Web service：

> java -cp sample hello.Hello

访问http://localhost:8080/hello?wsdl就会看到对应web service的wsdl

Java 6 SDK 内嵌了一个轻量级的 HTTP Server，方便开发者验证简单的 Web service 功能。通过以上三步，一个 Web service Endpoint 就部署完成，下面将开发一个调用 Hello 服务的客户端。

1.为 Web 服务的客户端产生存根文件：

> wsimport -p sample -keep http://localhost:8080/hello?wsdl

根据上面 URL 指向的 WSDL 文件，通过 JAXB 技术，生成了相应的 Java 对象。

2.开发，编译，运行 Web 服务客户程序：

```java
class HelloApp { 
   public static void main(String args[]) { 
       HelloService service = new HelloService(); 
       Hello helloProxy = service.getHelloPort(); 
       String hello = helloProxy.hello("developer works"); 
       System.out.println(hello); 
   } 
}
```

3.编译运行

输出：Hello, developer works

在 Java 6 SDK 中，Web 服务的开发过程被大大简化了。

---

[参考资源-Java SE 6 新特性系列](https://www.ibm.com/developerworks/cn/java/j-lo-jse6)
