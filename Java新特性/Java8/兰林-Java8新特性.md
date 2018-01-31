# Java 8 新特性

新特性列表：

- 函数式接口
- Lambda 表达式
- 接口的增强
- 集合之流式操作
- 注解的更新
- 安全性
- IO/NIO 的改进
- 全球化功能

## 函数式接口

Java 8 引入的一个核心概念是函数式接口（Functional Interfaces）。通过在接口里面添加一个抽象方法，这些方法可以直接从接口中运行。**如果一个接口定义个唯一一个抽象方法，那么这个接口就成为函数式接口。**同时，引入了一个新的注解：@FunctionalInterface。可以把他它放在一个接口前，表示这个接口是一个函数式接口。这个注解是非必须的，只要接口只包含一个方法的接口，虚拟机会自动判断，不过最好在接口上使用注解 @FunctionalInterface 进行声明。在接口中添加了 @FunctionalInterface 的接口，只允许有一个抽象方法，否则编译器也会报错。

函数式接口，有时候被称为SAM类型，意思是单抽象方法（Single Abstract Method）。

函数式接口示例：

```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

## Lambda 表达式

### 概述

Lambda表达式本质上就是一个匿名（即为命名）方法。这个方法不是独立运行的，而是用于实现由函数式接口定义的另一个方法。因此，Lambda表达式会导致产生一个匿名类。Lambda表达式也被称为闭包。

Lambda表达式只能用于其目标类型已被指定的上下文中。

函数式接口的重要属性是：我们能够使用 Lambda 实例化它们，Lambda 表达式让你能够将函数作为方法参数，或者将代码作为数据对待。Lambda 表达式的引入给开发者带来了不少优点：在 Java 8 之前，匿名内部类，监听器和事件处理器的使用都显得很冗长，代码可读性很差，Lambda 表达式的应用则使代码变得更加紧凑，可读性增强；Lambda 表达式使并行操作大集合变得很方便，可以充分发挥多核 CPU 的优势，更易于为多核处理器编写代码；

### 基本用法

Lambda 表达式由三个部分组成：
1. 一个括号内用逗号分隔的形式参数，参数是函数式接口里面方法的参数；
2. 一个箭头符号：->；
3. 方法体，可以是表达式和代码块。语法如下：

a> 方法体为表达式，该表达式的值作为返回值返回

> (parameters) -> expression

b> 方法体为代码块，必须用 {} 来包裹起来，且需要一个 return 返回值，但若函数式接口里面方法返回值是 void，则无需返回值。

> (parameters) -> { statements; }

下面是使用匿名内部类和 Lambda 表达式的代码比较。

```java
public class Main {
    public static void main(String[] args) {
        Button button = new Button();
        // 使用匿名内部类绑定ActionListener
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print("Helllo Lambda in actionPerformed");
            }
        });
        
        // 使用Lambda表达式绑定ActionListener
        button.addActionListener(
                // actionPerformed 有一个参数 e 传入，所以用(ActionEvent e)
                (ActionEvent e) -> System.out
                        .print("Helllo Lambda in actionPerformed"));
    }
}
```

如果没有参数则只需 ( )，例如 Thread 中的 run 方法就没有参数传入，当它使用 Lambda 表达式后：

```java
public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(
            // run 没有参数传入，所以用 (), 后面用 {} 包起方法体
            () -> {
                System.out.println("Hello from a thread in run");
            });
    }
}
```

为了进一步简化 Lambda 表达式，可以使用方法引用。例如，下面三种分别是使用内部类，使用 Lambda 表示式和使用方法引用方式的比较：

```java
public class Main {
    public static void main(String[] args) {
        // 1.使用内部类
        Function<Integer, String> f = new Function<Integer, String>() {
            @Override
            public String apply(Integer t) {
                return null;
            }
        };
        // 2.使用 Lambda 表达式
        Function<Integer, String> f2 = (Integer i) -> String.valueOf(i);
        // 3.使用方法引用的方式
        Function<Integer, String> f1 = String::valueOf;
    }
}
```

要使用 Lambda 表达式，需要定义一个函数式接口，这样往往会让程序充斥着过量的仅为 Lambda 表达式服务的函数式接口。为了减少这样过量的函数式接口，Java 8 在 java.util.function 中增加了不少新的函数式通用接口。例如：

- Function<T, R>：将 T 作为输入，返回 R 作为输出，他还包含了和其他函数组合的默认方法。
- Predicate<T> ：将 T 作为输入，返回一个布尔值作为输出，该接口包含多种默认方法来将Predicate 组合成其他复杂的逻辑（与、或、非）。
- Consumer<T> ：将 T 作为输入，不返回任何内容，表示在单个参数上的操作。

例如，People 类中有一个方法 getMaleList 需要获取男性的列表，这里需要定义一个函数式接口 PersonInterface：

```java
@FunctionalInterface
public interface PersonInterface {
    public boolean test(Person person);
}


public class People {
    private List<Person> persons = new ArrayList<Person>();

    public List<Person> getMaleList(PersonInterface filter) {
        List<Person> res = new ArrayList<Person>();
        persons.forEach((Person person) -> {
            if (filter.test(person)) {//调用 PersonInterface 的方法
                res.add(person);
            }
        });
        return res;
    }
}
```

为了去除 PersonInterface 这个函数式接口，可以用通用函数式接口 Predicate 替代如下：

```java
public class People {
    private List<Person> persons = new ArrayList<Person>();

    public List<Person> getMaleList(Predicate<Person> predicate) {
        List<Person> res = new ArrayList<Person>();
        persons.forEach(person -> {
            if (predicate.test(person)) {//调用 Predicate 的抽象方法 test
                res.add(person);
            }
        });
        return res;
    }
}
```

### 异常

Lambda表达式可以抛出异常。但是，如果抛出经检查的异常，该异常就必须与函数式接口的抽象方法的throws子句中列出的异常兼容。

```java
interface ICalculation {
    double calculate(int n) throws RuntimeException;
}

public class T2 {
    public static void main(String[] args) {
        ICalculation cal = (n) -> {
            if(n == 0){
                //编译不通过
                //throw new Exception("除数为0");
                throw new ArithmeticException("除数为0");
            }
            return 100 / n;
        };
        System.out.println(cal.calculate(0));
    }
}
```

### 变量捕获

在Lambda表达式中，可以访问其外层作用于内定义的变量。也可以显示或隐式地访问this变量，该变量引用Lambda表达式的外层类的调用实例。因此，Lambda表达式可以获取或设置其外层类的实例或静态变量的值，以及调用其外层类定义的方法。

但是，当Lambda表达式使用其外层作用域内定义的局部变量时，会产生一种特殊的情况，称谓“变量捕获”。这种情况下，Lambda表达式只能使用实质上final的局部变量。实质上final的变量是指在第一次赋值后，值不会再发生变化的变量。没有必要显示地将这种变量声明为final。**即Lambda表达式不能修改外层作用域的局部变量。**

### 方法引用

方法引用提供了一种引用而不执行方法的方式。这种特性与Lambda表达式相关，因为它也需要由兼容的函数式接口构成的目标类型上下文。计算时，方法引用也会创建函数式接口的一个实例。

**静态方法的方法引用**

创建静态方法引用语法如下：

ClassName::methodName

类名与方法名之间使用双冒号隔开（::是Java 8新增的分隔符，专门用于方法引用）。在与目标类型兼容的任何地方，都可以使用这个方法引用。

```java
interface ICalculation {
    double calculate(int n) throws RuntimeException;
}

class NumOps {
    static long cal(int i){
        return 100 * i;
    }
}

public class T2 {
    public static void main(String[] args) {
        ICalculation cal = NumOps::cal;
        System.out.println(cal.calculate(1000000000));
        // 输出：1.215752192E9
    }
}
```

**实例方法的方法引用**

实例方法的引用语法如下：

objRef::methodName

这种语法与静态方法的语法类似，只不过使用对象引用而不是类名。

```java
interface ICalculation {
    double calculate(int n) throws RuntimeException;
}

class NumOps {
    long cal(int i) {
        return 100 * i;
    }

}

public class T2 {
    public static void main(String[] args) {
        ICalculation cal = new NumOps()::cal;
        System.out.println(cal.calculate(1000000000));
        // 输出：1.215752192E9
    }
}
```

除此之外，还有多种引用的使用方式，如下：

- ClassName::instanceMethodName 函数式接口的第一个参数匹配调用对象，第二个参数匹配指定参数
- super::name 同上
- 泛型引用 ClassName::<Integer>methodName
- 构造函数引用 classname::new

## 接口的增强

Java 8 对接口做了进一步的增强。在接口中可以添加使用 default 关键字修饰的非抽象方法。还可以在接口中定义静态方法

### 默认方法

Java 8 还允许我们给接口添加一个非抽象的方法实现，只需要使用 default 关键字即可，这个特征又叫做扩展方法。在实现该接口时，该默认扩展方法在子类上可以直接使用，它的使用方式类似于抽象类中非抽象成员方法。但扩展方法不能够重载 Object 中的方法。例如：toString、equals、 hashCode 不能在接口中被重载。

```java
public interface DefaultFunInterface {
    // 定义默认方法
    default int count(){
        return 1;
    }
    // 可以有多个默认方法
    default int sum(){
        return 2;
    }
}


public class SubDefaultFunClass implements DefaultFunInterface {
    public static void main(String[] args) {
        // 实例化一个子类对象，改子类对象可以直接调用父接口中的默认方法count
        SubDefaultFunClass sub = new SubDefaultFunClass();
        System.out.println(sub.count());// 1
    }
}
```

### 静态方法

在接口中，还允许定义静态的方法。接口中的静态方法可以直接用接口来调用。

```java
public interface StaticFunInterface {
    public static int find(){
        return 1;
    }
}


public class TestStaticFun {
    public static void main(String[] args){
        //接口中定义了静态方法 find 直接被调用
        System.out.println(StaticFunInterface.find());// 1
    }
}
```

## 集合之流式操作

Java 8 引入了流式操作（Stream），通过该操作可以实现对集合（Collection）的并行处理和函数式操作。根据操作返回的结果不同，流式操作分为中间操作和最终操作两种。最终操作返回一特定类型的结果，而中间操作返回流本身，这样就可以将多个操作依次串联起来。根据流的并发性，流又可以分为串行和并行两种。流式操作实现了集合的过滤、排序、映射等功能。

Stream 和 Collection 集合的区别：Collection 是一种静态的内存数据结构，而 Stream 是有关计算的。前者是主要面向内存，存储在内存中，后者主要是面向 CPU，通过 CPU 实现计算。

### 串行和并行的流

流有串行和并行两种，串行流上的操作是在一个线程中依次完成，而并行流则是在多个线程上同时执行。并行与串行的流可以相互切换：通过 stream.sequential() 返回串行的流，通过 stream.parallel() 返回并行的流。相比较串行的流，并行的流可以很大程度上提高程序的执行效率。

```java
public class T1 {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 100000; i++) {
            list.add((int) (Math.random() * 100000));
        }

        long start = System.nanoTime();//获取系统开始排序的时间点
        sequentialSorted(list);
        parallelSorted(list);
        long end = System.nanoTime();//获取系统结束排序的时间点
        long ms = TimeUnit.NANOSECONDS.toMillis(end - start);//得到排序所用的时间
        System.out.println(ms + "ms");
    }

    /**
     *  串行排序
     * @param list
     */
    public static void sequentialSorted(List list) {
        System.out.println(((Stream) list.stream().sequential()).sorted().count());
    }

    /**
     * 并行排序
     * @param list
     */
    public static void parallelSorted(List list) {
        System.out.println(((Stream) list.stream().parallel()).sorted().count());
    }
}
```

### 中间操作

该操作会保持 stream 处于中间状态，允许做进一步的操作。它返回的还是的 Stream，允许更多的链式操作。常见的中间操作有：

- filter()：对元素进行过滤；
- sorted()：对元素排序；
- map()：元素的映射；
- distinct()：去除重复元素；
- subStream()：获取子 Stream 等；

```java
public class T2 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 100000; i++) {
            list.add(String.valueOf(Math.random() * 100000));
        }
        list.stream().filter((s) -> s.startsWith("9")).forEach(System.out::println);
    }
}
```

### 终止操作

该操作必须是流的最后一个操作，一旦被调用，Stream 就到了一个终止状态，而且不能再使用了。常见的终止操作有：

- forEach()：对每个元素做处理；
- toArray()：把元素导出到数组；
- findFirst()：返回第一个匹配的元素；
- anyMatch()：是否有匹配的元素等;

示例同上，`forEach()`就是一个终止操作

## 注解的更新

对于注解，Java 8 主要有两点改进：类型注解和重复注解

Java 8 的类型注解扩展了注解使用的范围。在该版本之前，注解只能是在声明的地方使用。现在几乎可以为任何东西添加注解：局部变量、类与接口，就连方法的异常也能添加注解。新增的两个注释的程序元素类型 `ElementType.TYPE_USE` 和 `ElementType.TYPE_PARAMETER` 用来描述注解的新场合。

- `ElementType.TYPE_PARAMETER` 表示该注解能写在类型变量的声明语句中。
- `ElementType.TYPE_USE `表示该注解能写在使用类型的任何语句中（例如声明语句、泛型和强制转换语句中的类型）。

对类型注解的支持，增强了通过静态分析工具发现错误的能力。原先只能在运行时发现的问题可以提前在编译的时候被排查出来。Java 8 本身虽然没有自带类型检测的框架，但可以通过使用 Checker Framework 这样的第三方工具，自动检查和确认软件的缺陷，提高生产效率。

用 @Repeatable 重复注解的例子：

```java
@Retention(RetentionPolicy.RUNTIME)
@interface Annots {
    Annot[] value();
}

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Annots.class)
@interface Annot {
    String value();
}

@Annot("a1")
@Annot("a2")
public class Test {
    public static void main(String[] args) {
        Annots annots1 = Test.class.getAnnotation(Annots.class);
        System.out.println(annots1.value()[0] + "," + annots1.value()[1]);
        // 输出: @Annot(value=a1),@Annot(value=a2)
        Annot[] annots2 = Test.class.getAnnotationsByType(Annot.class);
        System.out.println(annots2[0] + "," + annots2[1]);
        // 输出: @Annot(value=a1),@Annot(value=a2)
    }
}
```

注释 Annot 被 @Repeatable( Annots.class ) 注解。Annots 只是一个容器，它包含 Annot 数组, 编译器尽力向程序员隐藏它的存在。通过这样的方式，Test 类可以被 Annot 注解两次。重复注释的类型可以通过 getAnnotationsByType() 方法来返回

## 安全性

Java 8 在安全性上对许多方面进行了增强，列举部分：

支持更强的基于密码的加密算法。基于 AES 的加密算法，例如 PBEWithSHA256AndAES_128 和 PBEWithSHA512AndAES_256，已经被加入进来。

在客户端，TLS1.1 和 TLS1.2 被设为默认启动。并且可以通过新的系统属性包 jdk.tls.client.protocols 来对它进行配置。

Keystore 的增强，包含新的 Keystore 类型 java.security.DomainLoadStoreParameter 和为 Keytool 这个安全钥匙和证书的管理工具添加新的命令行选项-importpassword。同时，添加和更新了一些关于安全性的 API 来支持 KeyStore 的更新。

支持安全的随机数发生器。如果随机数来源于随机性不高的种子，那么那些用随机数来产生密钥或者散列敏感信息的系统就更易受攻击。SecureRandom 这个类的 getInstanceStrong 方法如今可以获取各个平台最强的随机数对象实例，通过这个实例生成像 RSA 私钥和公钥这样具有较高熵的随机数。

JSSE（Java(TM) Secure Socket Extension）服务器端开始支持 SSL/TLS 服务器名字识别 SNI（Server Name Indication）扩展。SNI 扩展目的是 SSL/TLS 协议可以通过 SNI 扩展来识别客户端试图通过握手协议连接的服务器名字。在 Java 7 中只在客户端默认启动 SNI 扩展。如今，在 JSSE 服务器端也开始支持 SNI 扩展了。

安全性比较差的加密方法被默认禁用。默认不支持 DES 相关的 Kerberos 5 加密方法。如果一定要使用这类弱加密方法需要在 krb5.conf 文件中添加 allow_weak_crypto=true。考虑到这类加密方法安全性极差，开发者应该尽量避免使用它。

## IO/NIO 的改进

Java 8 对 IO/NIO 也做了一些改进。主要包括：改进了 java.nio.charset.Charset 的实现，使编码和解码的效率得以提升，也精简了 jre/lib/charsets.jar 包；优化了 String(byte[],*) 构造方法和 String.getBytes() 方法的性能；还增加了一些新的 IO/NIO 方法，使用这些方法可以从文件或者输入流中获取流（java.util.stream.Stream），通过对流的操作，可以简化文本行处理、目录遍历和文件查找

新增的 API 如下：

- BufferedReader.line()：返回文本行的流 Stream<String>
- File.lines(Path, Charset)：返回文本行的流 Stream<String>
- File.list(Path)：遍历当前目录下的文件和目录
- File.walk(Path, int, FileVisitOption)：遍历某一个目录下的所有文件和指定深度的子目录
- File.find(Path, int, BiPredicate, FileVisitOption... )：查找相应的文件

用流式操作列出当前目录下的所有文件和目录示例：

```java
public class Test {
    public static void main(String[] args) throws IOException {
        Files.list(new File(".").toPath()).forEach(System.out::println);
        // 输出：
        // .\.idea
        // .\pom.xml
        // .\src
        // .\target
        // .\test.iml
    }
}
```

## 全球化功能

Java 8 版本还完善了全球化功能：支持新的 Unicode 6.2.0 标准，新增了日历和本地化的 API，改进了日期时间的管理等。

Java 的日期与时间 API 问题由来已久，Java 8 之前的版本中关于时间、日期及其他时间日期格式化类由于线程安全、重量级、序列化成本高等问题而饱受批评。Java 8 吸收了 Joda-Time 的精华，以一个新的开始为 Java 创建优秀的 API。新的 java.time 中包含了所有关于时钟（Clock），本地日期（LocalDate）、本地时间（LocalTime）、本地日期时间（LocalDateTime）、时区（ZonedDateTime）和持续时间（Duration）的类。历史悠久的 Date 类新增了 toInstant() 方法，用于把 Date 转换成新的表示形式。这些新增的本地化时间日期 API 大大简化了了日期时间和本地化的管理。

对 LocalDate，LocalTime 的简单应用示例：

```java
public class T3 {
    public static void main(String[] args) {
        //LocalDate
        LocalDate localDate = LocalDate.now(); //获取本地日期
        localDate = LocalDate.ofYearDay(2017, 200); // 获得 2017 年的第 200 天
        System.out.println(localDate.toString());//输出：2017-07-19
        localDate = LocalDate.of(2017, Month.SEPTEMBER, 10); //2017 年 9 月 10 日
        System.out.println(localDate.toString());//输出：2017-09-10
        //LocalTime
        LocalTime localTime = LocalTime.now(); //获取当前时间
        System.out.println(localTime.toString());//输出当前时间
        localTime = LocalTime.of(10, 20, 50);//获得 10:20:50 的时间点
        System.out.println(localTime.toString());//输出: 10:20:50
        //Clock 时钟
        Clock clock = Clock.systemDefaultZone();//获取系统默认时区 (当前瞬时时间 )
        long millis = clock.millis();
        // 输出：
        // 2017-07-19
        // 2017-09-10
        // 18:09:51.088
        // 10:20:50 
    }
}
```

---

资料来源

[Java 8 新特性概述](https://www.ibm.com/developerworks/cn/java/j-lo-jdk8newfeature/index.html)

[JDK 8 新增功能](http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html)