# 明辉


## JDK1.5新特性：

### **1.自动装箱与拆箱：**

自动装箱的过程：每当需要一种类型的对象时，这种基本类型就自动地封装到与它相同类型的包装中。

自动拆箱的过程：每当需要一个值时，被装箱对象中的值就被自动地提取出来，没必要再去调用intValue()和doubleValue()方法。

自动装箱，只需将该值赋给一个类型包装器引用，java会自动创建一个对象。

自动拆箱，只需将该对象值赋给一个基本类型即可。

java——类的包装器

类型包装器有：Double,Float,Long,Integer,Short,Character和Boolean

### **2.枚举**

把集合里的对象元素一个一个提取出来。枚举类型使代码更具可读性，理解清晰，易于维护。枚举类型是强类型的，从而保证了系统安全性。而以类的静态字段实现的类似替代模型，不具有枚举的简单性和类型安全性。

简单的用法：JavaEnum简单的用法一般用于代表一组常用常量，可用来代表一类相同类型的常量值。

复杂用法：Java为枚举类型提供了一些内置的方法，同事枚举常量还可以有自己的方法。可以很方便的遍历枚举对象。

### **3.静态导入**

通过使用 import static，就可以不用指定 Constants 类名而直接使用静态成员，包括静态方法。

import xxxx 和 import static xxxx的区别是前者一般导入的是类文件如import java.util.Scanner;后者一般是导入静态的方法，import static java.lang.System.out。

### **4.可变参数（Varargs）**

可变参数的简单语法格式为：

methodName([argumentList], dataType...argumentName);

### **5.内省（Introspector）**

是 Java语言对Bean类属性、事件的一种缺省处理方法。例如类A中有属性name,那我们可以通过getName,setName来得到其值或者设置新 的值。通过getName/setName来访问name属性，这就是默认的规则。Java中提供了一套API用来访问某个属性的getter /setter方法，通过这些API可以使你不需要了解这个规则（但你最好还是要搞清楚），这些API存放于包java.beans中。

一 般的做法是通过类Introspector来获取某个对象的BeanInfo信息，然后通过BeanInfo来获取属性的描述器 （PropertyDescriptor），通过这个属性描述器就可以获取某个属性对应的getter/setter方法，然后我们就可以通过反射机制来 调用这些方法。


###   **6.泛型(Generic) **

C++ 通过模板技术可以指定集合的元素类型，而Java在1.5之前一直没有相对应的功能。一个集合可以放任何类型的对象，相应地从集合里面拿对象的时候我们也 不得不对他们进行强制得类型转换。猛虎引入了泛型，它允许指定集合里元素的类型，这样你可以得到强类型在编译时刻进行类型检查的好处。

### **7.For-Each循环 **

For-Each循环得加入简化了集合的遍历。假设我们要遍历一个集合对其中的元素进行一些处理。

## JDK 1.6新特性（转）

有关JDK1.6的新特性reamerit的博客文章已经说的很详细了。

### **1.Desktop类和SystemTray类 **

在JDK6中 ,AWT新增加了两个类:Desktop和SystemTray。 

前者可以用来打开系统默认浏览器浏览指定的URL,打开系统默认邮件客户端给指定的邮箱发邮件,用默认应用程序打开或编辑文件(比如,用记事本打开以txt为后缀名的文件),用系统默认的打印机打印文档;后者可以用来在系统托盘区创建一个托盘程序. 

### **2.使用JAXB2来实现对象与XML之间的映射 **

JAXB是Java Architecture for XML Binding的缩写，可以将一个Java对象转变成为XML格式，反之亦然。 

我 们把对象与关系数据库之间的映射称为ORM, 其实也可以把对象与XML之间的映射称为OXM(Object XML Mapping). 原来JAXB是Java EE的一部分，在JDK6中，SUN将其放到了Java SE中，这也是SUN的一贯做法。JDK6中自带的这个JAXB版本是2.0, 比起1.0(JSR 31)来，JAXB2(JSR 222)用JDK5的新特性Annotation来标识要作绑定的类和属性等，这就极大简化了开发的工作量。 

实 际上，在Java EE 5.0中，EJB和Web Services也通过Annotation来简化开发工作。另外,JAXB2在底层是用StAX(JSR 173)来处理XML文档。除了JAXB之外，我们还可以通过XMLBeans和Castor等来实现同样的功能。 

### **3.理解StAX **

StAX(JSR 173)是JDK6.0中除了DOM和SAX之外的又一种处理XML文档的API。 

StAX 的来历 ：在JAXP1.3(JSR 206)有两种处理XML文档的方法:DOM(Document Object Model)和SAX(Simple API for XML). 

由 于JDK6.0中的JAXB2(JSR 222)和JAX-WS 2.0(JSR 224)都会用到StAX所以Sun决定把StAX加入到JAXP家族当中来，并将JAXP的版本升级到1.4(JAXP1.4是JAXP1.3的维护版 本). JDK6里面JAXP的版本就是1.4\. 。 

StAX是The Streaming API for XML的缩写，一种利用拉模式解析(pull-parsing)XML文档的API.StAX通过提供一种基于事件迭代器(Iterator)的API让 程序员去控制xml文档解析过程,程序遍历这个事件迭代器去处理每一个解析事件，解析事件可以看做是程序拉出来的，也就是程序促使解析器产生一个解析事件 然后处理该事件，之后又促使解析器产生下一个解析事件，如此循环直到碰到文档结束符； 

SAX也是基于事件处理xml文档，但却 是用推模式解析，解析器解析完整个xml文档后，才产生解析事件，然后推给程序去处理这些事件；DOM 采用的方式是将整个xml文档映射到一颗内存树，这样就可以很容易地得到父节点和子结点以及兄弟节点的数据，但如果文档很大，将会严重影响性能。 

### **4.使用Compiler API **

现在我 们可以用JDK6 的Compiler API(JSR 199)去动态编译Java源文件，Compiler API结合反射功能就可以实现动态的产生Java代码并编译执行这些代码，有点动态语言的特征。 

这 个特性对于某些需要用到动态编译的应用程序相当有用， 比如JSP Web Server，当我们手动修改JSP后，是不希望需要重启Web Server才可以看到效果的，这时候我们就可以用Compiler API来实现动态编译JSP文件，当然，现在的JSP Web Server也是支持JSP热部署的，现在的JSP Web Server通过在运行期间通过Runtime.exec或ProcessBuilder来调用javac来编译代码，这种方式需要我们产生另一个进程去 做编译工作，不够优雅而且容易使代码依赖与特定的操作系统；Compiler API通过一套易用的标准的API提供了更加丰富的方式去做动态编译,而且是跨平台的。 

### **5.轻量级Http Server API **

JDK6 提供了一个简单的Http Server API,据此我们可以构建自己的嵌入式Http Server,它支持Http和Https协议,提供了HTTP1.1的部分实现，没有被实现的那部分可以通过扩展已有的Http Server API来实现,程序员必须自己实现HttpHandler接口,HttpServer会调用HttpHandler实现类的回调方法来处理客户端请求,在 这里,我们把一个Http请求和它的响应称为一个交换,包装成HttpExchange类,HttpServer负责将HttpExchange传给 HttpHandler实现类的回调方法. 

### **6.插入式注解处理API(Pluggable Annotation Processing API) **

插入式注解处理API(JSR 269)提供一套标准API来处理Annotations(JSR 175) 

实 际上JSR 269不仅仅用来处理Annotation,我觉得更强大的功能是它建立了Java 语言本身的一个模型,它把method, package, constructor, type, variable, enum, annotation等Java语言元素映射为Types和Elements(两者有什么区别?), 从而将Java语言的语义映射成为对象, 我们可以在javax.lang.model包下面可以看到这些类. 所以我们可以利用JSR 269提供的API来构建一个功能丰富的元编程(metaprogramming)环境. 

JSR 269用Annotation Processor在编译期间而不是运行期间处理Annotation, Annotation Processor相当于编译器的一个插件,所以称为插入式注解处理.如果Annotation Processor处理Annotation时(执行process方法)产生了新的Java代码,编译器会再调用一次Annotation Processor,如果第二次处理还有新代码产生,就会接着调用Annotation Processor,直到没有新代码产生为止.每执行一次process()方法被称为一个"round",这样整个Annotation processing过程可以看作是一个round的序列. 

JSR 269主要被设计成为针对Tools或者容器的API. 举个例子,我们想建立一套基于Annotation的单元测试框架(如TestNG),在测试类里面用Annotation来标识测试期间需要执行的测试方法。 

### **7.用Console开发控制台程序 **

JDK6 中提供了java.io.Console 类专用来访问基于字符的控制台设备. 你的程序如果要与Windows下的cmd或者Linux下的Terminal交互,就可以用Console类代劳. 但我们不总是能得到可用的Console, 一个JVM是否有可用的Console依赖于底层平台和JVM如何被调用. 如果JVM是在交互式命令行(比如Windows的cmd)中启动的,并且输入输出没有重定向到另外的地方,那么就可以得到一个可用的Console实 例. 

### **8.对脚本语言的支持如: ruby, groovy, javascript. **

### **9.Common Annotations **

Common annotations原本是Java EE 5.0(JSR 244)规范的一部分，现在SUN把它的一部分放到了Java SE 6.0中. 

随 着Annotation元数据功能(JSR 175)加入到Java SE 5.0里面，很多Java 技术(比如EJB,Web Services)都会用Annotation部分代替XML文件来配置运行参数（或者说是支持声明式编程,如EJB的声明式事务）, 如果这些技术为通用目的都单独定义了自己的Annotations,显然有点重复建设, 所以,为其他相关的Java技术定义一套公共的Annotation是有价值的，可以避免重复建设的同时，也保证Java SE和Java EE 各种技术的一致性. 

下面列举出Common Annotations 1.0里面的10个Annotations Common Annotations 

Annotation Retention Target Description 

Generated Source ANNOTATION_TYPE, CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE 用于标注生成的源代码 

Resource Runtime TYPE, METHOD, FIELD 用于标注所依赖的资源,容器据此注入外部资源依赖，有基于字段的注入和基于setter方法的注入两种方式 

Resources Runtime TYPE 同时标注多个外部依赖，容器会把所有这些外部依赖注入 

PostConstruct Runtime METHOD 标注当容器注入所有依赖之后运行的方法，用来进行依赖注入后的初始化工作，只有一个方法可以标注为PostConstruct 

PreDestroy Runtime METHOD 当对象实例将要被从容器当中删掉之前，要执行的回调方法要标注为PreDestroy RunAs Runtime TYPE 用于标注用什么安全角色来执行被标注类的方法，这个安全角色必须和Container 的Security角色一致的。RolesAllowed Runtime TYPE, METHOD 用于标注允许执行被标注类或方法的安全角色，这个安全角色必须和Container 的Security角色一致的 

PermitAll Runtime TYPE, METHOD 允许所有角色执行被标注的类或方法 

DenyAll Runtime TYPE, METHOD 不允许任何角色执行被标注的类或方法，表明该类或方法不能在Java EE容器里面运行 

DeclareRoles Runtime TYPE 用来定义可以被应用程序检验的安全角色，通常用isUserInRole来检验安全角色 

注意: 

1.RolesAllowed,PermitAll,DenyAll不能同时应用到一个类或方法上 

2.标注在方法上的RolesAllowed,PermitAll,DenyAll会覆盖标注在类上的RolesAllowed,PermitAll,DenyAll 

3.RunAs,RolesAllowed,PermitAll,DenyAll和DeclareRoles还没有加到Java SE 6.0上来 

4\. 处理以上Annotations的工作是由Java EE容器来做, Java SE 6.0只是包含了上面表格的前五种Annotations的定义类,并没有包含处理这些Annotations的引擎,这个工作可以由Pluggable Annotation Processing API(JSR 269)来做

改动的地方最大的就是java GUI界面的显示了，JDK6.0（也就是JDK1.6）支持最新的windows vista系统的Windows Aero视窗效果，而JDK1.5不支持！！！ 

你要在vista环境下编程的话最好装jdk6.0，否则它总是换到windows basic视窗效果.