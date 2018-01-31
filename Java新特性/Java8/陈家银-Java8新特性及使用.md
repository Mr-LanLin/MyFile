# Java8新特性及使用

## 新特性列表

以下是Java8中的引入的部分新特性。关于Java8新特性更详细的介绍可参考[这里](http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html)。

- 方法引用
- 接口默认方法和静态方法
- Lambda 表达式
- 函数式接口
- 重复注解
- 扩展注解的支持
- Stream
- Optional
- Date/Time API
- Base64
- JavaFX
- 其它
  - JDBC4.2规范
  - 更好的类型推测机制
  - HashMap性能提升
  - IO/NIO 的改进
  - JavaScript引擎Nashorn
  - 并发（Concurrency）
  - 类依赖分析器jdeps
  - JVM的PermGen空间被移除

## 一、方法引用

方法引用是使用一对冒号`::`并通过方法的名字来指向一个方法。方法引用可以使语言的构造更紧凑简洁，减少冗余代码。

下面，我们在`Car`类中定义了`4`个方法作为例子来区分 Java 中`4`种不同方法的引用。

```java
class Car {

    // Supplier是jdk1.8的接口，这里和Lamda一起使用了
    public static Car create(final Supplier<Car> supplier) {
        return supplier.get();
    }

    public static void collide(final Car car) {
        System.out.println("Collided " + car.toString());
    }

    public void follow(final Car another) {
        System.out.println("Following the " + another.toString());
    }

    public void repair() {
        System.out.println("Repaired " + this.toString());
    }

}
```

### 1. 构造器引用

它的语法是`Class::new`，或者更一般的`Class<T>::new`实例如下：

```java
final Car car = Car.create(Car::new);
final List<Car> cars = Arrays.asList(car);
```

### 2. 静态方法引用

它的语法是`Class::static_method`，实例如下：

```java
cars.forEach(Car::collide);
```

### 3. 特定类的任意对象的方法引用

它的语法是`Class::method`实例如下：

```java
cars.forEach(Car::repair);
```

### 4. 特定对象的方法引用

它的语法是`instance::method`实例如下：

```java
final Car police = Car.create(Car::new);
cars.forEach(police::follow);
```

以下是方法引用的实例：

```java
import java.util.ArrayList;
import java.util.List;

/**
 * Tester.
 *
 * @author blinkfox on 2018-01-05.
 */
public class Tester {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("张三");
        list.add("李四");
        list.add("王五");

        list.forEach(System.out::println);
    }

}
```

实例中我们将`System.out::println`方法作为静态方法来引用。执行以上程序，输出结果为：

```bash
张三
李四
王五
```

## 二、接口默认方法和静态方法

Java 8用默认方法与静态方法这两个新概念来扩展接口的声明。与传统的接口又有些不一样，它允许在已有的接口中添加新方法，而同时又保持了与旧版本代码的兼容性。

### 1. 接口默认方法

默认方法与抽象方法不同之处在于抽象方法必须要求实现，但是默认方法则没有这个要求。相反，每个接口都必须提供一个所谓的默认实现，这样所有的接口实现者将会默认继承它（如果有必要的话，可以覆盖这个默认实现）。让我们看看下面的例子：

```java
private interface Defaulable {
    // Interfaces now allow default methods, the implementer may or
    // may not implement (override) them.
    default String notRequired() {
        return "Default implementation";
    }
}

private static class DefaultableImpl implements Defaulable {
}

private static class OverridableImpl implements Defaulable {
    @Override
    public String notRequired() {
        return "Overridden implementation";
    }
}
```

`Defaulable`接口用关键字`default`声明了一个默认方法`notRequired()`，`Defaulable`接口的实现者之一`DefaultableImpl`实现了这个接口，并且让默认方法保持原样。`Defaulable`接口的另一个实现者`OverridableImpl`用自己的方法覆盖了默认方法。

### 2. 接口静态方法

Java 8带来的另一个有趣的特性是接口可以声明（并且可以提供实现）静态方法。在接口中定义静态方法，使用static关键字，例如：

```java
private interface DefaulableFactory {
    // Interfaces now allow static methods
    static Defaulable create(Supplier<Defaulable> supplier) {
        return supplier.get();
    }
}
```

下面的一小段代码片段把上面的默认方法与静态方法黏合到一起。

```java
public static void main(String[] args) {
    Defaulable defaulable = DefaulableFactory.create(DefaultableImpl::new);
    System.out.println(defaulable.notRequired());

    defaulable = DefaulableFactory.create(OverridableImpl::new);
    System.out.println(defaulable.notRequired());
}
```

在JVM中，默认方法的实现是非常高效的，并且通过字节码指令为方法调用提供了支持。默认方法允许继续使用现有的Java接口，而同时能够保障正常的编译过程。这方面好的例子是大量的方法被添加到`java.util.Collection`接口中去：`stream()`，`parallelStream()`，`forEach()`，`removeIf()`等。

尽管默认方法非常强大，但是在使用默认方法时我们需要小心注意一个地方：在声明一个默认方法前，请仔细思考是不是真的有必要使用默认方法，因为默认方法会带给程序歧义，并且在复杂的继承体系中容易产生编译错误。

## 三、Lambda 表达式

`Lambda`表达式（也称为闭包）是整个Java 8发行版中最受期待的在Java语言层面上的改变，Lambda允许把函数作为一个方法的参数（函数作为参数传递进方法中）。

一个`Lambda`可以由用逗号分隔的参数列表、`–>`符号与函数体三部分表示。

首先看看在老版本的Java中是如何排列字符串的：

```java
List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
Collections.sort(names, new Comparator<String>() {

    @Override
    public int compare(String a, String b) {
        return b.compareTo(a);
    }

});
```

只需要给静态方法`Collections.sort`传入一个List对象以及一个比较器来按指定顺序排列。通常做法都是创建一个匿名的比较器对象然后将其传递给sort方法。
在Java 8 中你就没必要使用这种传统的匿名对象的方式了，Java 8提供了更简洁的语法，lambda表达式：

```java
Collections.sort(names, (String a, String b) -> {
    return b.compareTo(a);
});
```

看到了吧，代码变得更段且更具有可读性，但是实际上还可以写得更短：

```java
Collections.sort(names, (String a, String b) -> b.compareTo(a));
```

对于函数体只有一行代码的，你可以去掉大括号`{}`以及`return`关键字，但是你还可以写得更短点：

```java
Collections.sort(names, (a, b) -> b.compareTo(a));
```

Java编译器可以自动推导出参数类型，所以你可以不用再写一次类型。

## 四、函数式接口

`Lambda`表达式是如何在Java的类型系统中表示的呢？每一个Lambda表达式都对应一个类型，通常是接口类型。而**函数式接口**是指仅仅只包含一个抽象方法的接口，每一个该类型的Lambda表达式都会被匹配到这个抽象方法。因为**默认方法**不算抽象方法，所以你也可以给你的函数式接口添加默认方法。

我们可以将Lambda表达式当作任意只包含一个抽象方法的接口类型，确保你的接口一定达到这个要求，你只需要给你的接口添加`@FunctionalInterface`注解，编译器如果发现你标注了这个注解的接口有多于一个抽象方法的时候会报错的。

示例如下：

```java
@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}

Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
Integer converted = converter.convert("123");
System.out.println(converted); // 123
```

> **注**：如果`@FunctionalInterface`如果没有指定，上面的代码也是对的。

Java8 API包含了很多内建的函数式接口，在老Java中常用到的比如`Comparator`或者`Runnable`接口，这些接口都增加了`@FunctionalInterface`注解以便能用在`Lambda`上。

Java8 API同样还提供了很多全新的函数式接口来让工作更加方便，有一些接口是来自Google Guava库里的，即便你对这些很熟悉了，还是有必要看看这些是如何扩展到lambda上使用的。

### 1. Predicate 接口

`Predicate`接口只有一个参数，返回`boolean`类型。该接口包含多种默认方法来将`Predicate`组合成其他复杂的逻辑（比如：**与**，**或**，**非**）：

```java
Predicate<String> predicate = (s) -> s.length() > 0;
predicate.test("foo");            // true
predicate.negate().test("foo");     // false
Predicate<Boolean> nonNull = Objects::nonNull;
Predicate<Boolean> isNull = Objects::isNull;
Predicate<String> isEmpty = String::isEmpty;
Predicate<String> isNotEmpty = isEmpty.negate();
```

### 2. Function 接口

`Function`接口有一个参数并且返回一个结果，并附带了一些可以和其他函数组合的默认方法（`compose`, `andThen`）。代码如下:

```java
Function<String, Integer> toInteger = Integer::valueOf;
Function<String, String> backToString = toInteger.andThen(String::valueOf);
backToString.apply("123");     // "123"
```

### 3. Supplier 接口

`Supplier`接口返回一个任意范型的值，和Function接口不同的是该接口没有任何参数。代码如下:

```java
Supplier<Person> personSupplier = Person::new;
personSupplier.get();   // new Person
```

### 4. Consumer 接口

`Consumer`接口表示执行在单个参数上的操作。代码如下:

```java
Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
greeter.accept(new Person("Luke", "Skywalker"));
```

### 5. Comparator 接口

`Comparator`是老Java中的经典接口， Java 8在此之上添加了多种默认方法。代码如下:

```java
Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
Person p1 = new Person("John", "Doe");
Person p2 = new Person("Alice", "Wonderland");
comparator.compare(p1, p2);             // > 0
comparator.reversed().compare(p1, p2);  // < 0
```

### 6. Filter 过滤

过滤通过一个`predicate`接口来过滤并只保留符合条件的元素，该操作属于中间操作，所以我们可以在过滤后的结果来应用其他Stream操作（比如forEach）。forEach需要一个函数来对过滤后的元素依次执行。forEach是一个最终操作，所以我们不能在forEach之后来执行其他Stream操作。代码如下:

```java
stringCollection
    .stream()
    .filter((s) -> s.startsWith("a"))
    .forEach(System.out::println);
// "aaa2", "aaa1"
```

### 7. Sort 排序

排序是一个中间操作，返回的是排序好后的`Stream`。如果你不指定一个自定义的`Comparator`则会使用默认排序。代码如下:

```java
stringCollection
    .stream()
    .sorted()
    .filter((s) -> s.startsWith("a"))
    .forEach(System.out::println);
// "aaa1", "aaa2"
```

需要注意的是，排序只创建了一个排列好后的Stream，而不会影响原有的数据源，排序之后原数据`stringCollection`是不会被修改的:

```java
System.out.println(stringCollection);
// ddd2, aaa2, bbb1, aaa1, bbb3, ccc, bbb2, ddd1
```

### 8. Map 映射

中间操作`map`会将元素根据指定的`Function`接口来依次将元素转成另外的对象，下面的示例展示了将字符串转换为大写字符串。你也可以通过map来讲对象转换成其他类型，map返回的Stream类型是根据你map传递进去的函数的返回值决定的。代码如下:

```java
stringCollection
    .stream()
    .map(String::toUpperCase)
    .sorted((a, b) -> b.compareTo(a))
    .forEach(System.out::println);
// "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"
```

### 9. Match 匹配

`Stream`提供了多种匹配操作，允许检测指定的`Predicate`是否匹配整个`Stream`。所有的匹配操作都是最终操作，并返回一个`boolean`类型的值。代码如下:

```java
boolean anyStartsWithA = stringCollection
        .stream()
        .anyMatch((s) -> s.startsWith("a"));
System.out.println(anyStartsWithA);      // true
boolean allStartsWithA = stringCollection
        .stream()
        .allMatch((s) -> s.startsWith("a"));
System.out.println(allStartsWithA);      // false
boolean noneStartsWithZ = stringCollection
        .stream()
        .noneMatch((s) -> s.startsWith("z"));
System.out.println(noneStartsWithZ);      // true
```

### 10. Count 计数

计数是一个最终操作，返回Stream中元素的个数，返回值类型是`long`。代码如下:

```java
long startsWithB = stringCollection
        .stream()
        .filter((s) -> s.startsWith("b"))
        .count();
System.out.println(startsWithB);    // 3
```

### 11. Reduce 规约

这是一个最终操作，允许通过指定的函数来将`stream`中的多个元素规约为一个元素，规越后的结果是通过`Optional`接口表示的。代码如下:

```java
Optional<String> reduced = stringCollection
        .stream()
        .sorted()
        .reduce((s1, s2) -> s1 + "#" + s2);
reduced.ifPresent(System.out::println);
// "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"
```

## 五、重复注解

自从Java 5引入了注解机制，这一特性就变得非常流行并且广为使用。然而，使用注解的一个限制是相同的注解在同一位置只能声明一次，不能声明多次。Java 8打破了这条规则，引入了重复注解机制，这样相同的注解可以在同一地方声明多次。

重复注解机制本身必须用`@Repeatable`注解。事实上，这并不是语言层面上的改变，更多的是编译器的技巧，底层的原理保持不变。让我们看一个快速入门的例子：

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RepeatingAnnotations {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Filters {
        Filter[] value();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Filters.class)
    public @interface Filter {
        String value();
    };

    @Filter("filter1")
    @Filter("filter2")
    public interface Filterable {
    }

    public static void main(String[] args) {
        for(Filter filter: Filterable.class.getAnnotationsByType(Filter.class)) {
            System.out.println(filter.value());
        }
    }

}
```

正如我们看到的，这里有个使用`@Repeatable(Filters.class)`注解的注解类`Filter`，`Filters`仅仅是`Filter`注解的数组，但Java编译器并不想让程序员意识到`Filters`的存在。这样，接口`Filterable`就拥有了两次`Filter`（并没有提到`Filter`）注解。

同时，反射相关的API提供了新的函数`getAnnotationsByType()`来返回重复注解的类型（请注意`Filterable.class.getAnnotation(Filters.class`)`经编译器处理后将会返回Filters的实例）。

## 六、扩展注解的支持

Java 8扩展了注解的上下文。现在几乎可以为任何东西添加注解：局部变量、泛型类、父类与接口的实现，就连方法的异常也能添加注解。下面演示几个例子：

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;

public class Annotations {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
    public @interface NonEmpty {
    }

    public static class Holder<@NonEmpty T> extends @NonEmpty Object {
        public void method() throws @NonEmpty Exception {
        }
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        final Holder<String> holder = new @NonEmpty Holder<String>();
        @NonEmpty Collection<@NonEmpty String> strings = new ArrayList<>();
    }

}
```

## 七、Stream

最新添加的`Stream API(java.util.stream)`把真正的函数式编程风格引入到Java中。这是目前为止对Java类库最好的补充，因为`Stream API`可以极大提供Java程序员的生产力，让程序员写出高效率、干净、简洁的代码。

Stream API极大简化了集合框架的处理。让我们以一个简单的`Task`类为例进行介绍：

```java
public class Streams  {

    private enum Status {
        OPEN, CLOSED
    };

    private static final class Task {

        private final Status status;
        private final Integer points;

        Task(final Status status, final Integer points) {
            this.status = status;
            this.points = points;
        }

        public Integer getPoints() {
            return points;
        }

        public Status getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return String.format("[%s, %d]", status, points);
        }
    }

}
```

`Task`类中有一个分数的概念，其次是还有一个值可以为`OPEN`或`CLOSED`的状态.让我们引入一个`Task`的小集合作为演示例子：

```java
final Collection<Task> tasks = Arrays.asList(
    new Task(Status.OPEN, 5),
    new Task(Status.OPEN, 13),
    new Task(Status.CLOSED, 8)
);
```

我们下面要讨论的第一个问题是所有状态为`OPEN`的任务一共有多少分数？在Java 8以前，一般的解决方式用foreach循环，但是在Java 8里面我们可以使用`stream`：一串支持连续、并行聚集操作的元素。

```java
// Calculate total points of all active tasks using sum()
final long totalPointsOfOpenTasks = tasks
    .stream()
    .filter(task -> task.getStatus() == Status.OPEN)
    .mapToInt(Task::getPoints)
    .sum();

System.out.println("Total points: " + totalPointsOfOpenTasks);
```

程序在控制台上的输出如下：

```bash
Total points: 18
```

这里有几个注意事项。第一，task集合被转换化为其相应的`stream`表示。然后，`filter`操作过滤掉状态为`CLOSED`的task。下一步，`mapToInt`操作通过`Task::getPoints`这种方式调用每个task实例的`getPoints`方法把Task的stream转化为`Integer`的`stream`。最后，用`sum`函数把所有的分数加起来，得到最终的结果。

`.stream`操作被分成了中间操作与最终操作这两种。

中间操作返回一个新的`stream`对象。中间操作总是采用惰性求值方式，运行一个像filter这样的中间操作实际上没有进行任何过滤，相反它在遍历元素时会产生了一个新的stream对象，这个新的stream对象包含原始`stream`中符合给定谓词的所有元素。

像`forEach`、`sum`这样的最终操作可能直接遍历stream，产生一个结果或副作用。当最终操作执行结束之后，stream管道被认为已经被消耗了，没有可能再被使用了。在大多数情况下，最终操作都是采用及早求值方式，及早完成底层数据源的遍历。

stream另一个有价值的地方是能够原生支持并行处理。让我们来看看这个算task分数和的例子。

```java
// Calculate total points of all tasks
final double totalPoints = tasks
   .stream()
   .parallel()
   .map(task -> task.getPoints()) // or map(Task::getPoints)
   .reduce(0, Integer::sum);

System.out.println("Total points (all tasks): " + totalPoints);
```

这个例子和第一个例子很相似，但这个例子的不同之处在于这个程序是并行运行的，其次使用`reduce`方法来算最终的结果。

下面是这个例子在控制台的输出：

```java
Total points (all tasks): 26.0
```

经常会有这个一个需求：我们需要按照某种准则来对集合中的元素进行分组。`Stream`也可以处理这样的需求，下面是一个例子：

```java
// Group tasks by their status
final Map<Status, List<Task>> map = tasks
    .stream()
    .collect(Collectors.groupingBy(Task::getStatus));
System.out.println(map);
```

这个例子的控制台输出如下：

```java
{CLOSED=[[CLOSED, 8]], OPEN=[[OPEN, 5], [OPEN, 13]]}
```

让我们来计算整个集合中每个task分数（或权重）的平均值来结束task的例子。

```java
// Calculate the weight of each tasks (as percent of total points)
final Collection<String> result = tasks
    .stream()                                      // Stream<String>
    .mapToInt(Task::getPoints)                     // IntStream
    .asLongStream()                                // LongStream
    .mapToDouble(points -> points / totalPoints)   // DoubleStream
    .boxed()                                       // Stream<Double>
    .mapToLong(weigth -> (long) (weigth * 100))    // LongStream
    .mapToObj(percentage -> percentage + "%")      // Stream<String>
    .collect(Collectors.toList());                 // List<String>

System.out.println(result);
```

下面是这个例子的控制台输出：

```java
[19%, 50%, 30%]
```

最后，就像前面提到的，`Stream API`不仅仅处理Java集合框架。像从文本文件中逐行读取数据这样典型的I/O操作也很适合用Stream API来处理。下面用一个例子来应证这一点。

```java
final Path path = new File(filename).toPath();
try(Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
    lines.onClose(() -> System.out.println("Done!")).forEach(System.out::println);
}
```

对一个`stream`对象调用`onClose`方法会返回一个在原有功能基础上新增了关闭功能的stream对象，当对stream对象调用`close()`方法时，与关闭相关的处理器就会执行。

## 八、Optional

到目前为止，臭名昭著的空指针异常是导致Java应用程序失败的最常见原因。以前，为了解决空指针异常，Google公司著名的`Guava`项目引入了`Optional`类，Guava通过使用检查空值的方式来防止代码污染，它鼓励程序员写更干净的代码。受到Google Guava的启发，`Optional`类已经成为Java 8类库的一部分。

`Optional`实际上是个容器：它可以保存类型T的值，或者仅仅保存null。`Optional`提供很多有用的方法，这样我们就不用显式进行空值检测。

我们下面用两个小例子来演示如何使用Optional类：一个允许为空值，一个不允许为空值。

```java
Optional<String> fullName = Optional.ofNullable(null);
System.out.println("Full Name is set? " + fullName.isPresent());
System.out.println("Full Name: " + fullName.orElseGet(() -> "[none]"));
System.out.println(fullName.map(s -> "Hey " + s + "!").orElse("Hey Stranger!"));
```

如果`Optional`类的实例为非空值的话，`isPresent()`返回`true`，否从返回`false`。为了防止Optional为空值，`orElseGet()`方法通过回调函数来产生一个默认值。`map()`函数对当前`Optional`的值进行转化，然后返回一个新的`Optional`实例。`orElse()`方法和`orElseGet()`方法类似，但是`orElse`接受一个默认值而不是一个回调函数。下面是这个程序的输出：

```bash
Full Name is set? false
Full Name: [none]
Hey Stranger!
```

让我们来看看另一个例子：

```java
Optional<String> firstName = Optional.of("Tom");
System.out.println("First Name is set? " + firstName.isPresent());
System.out.println("First Name: " + firstName.orElseGet(() -> "[none]"));
System.out.println(firstName.map(s -> "Hey " + s + "!").orElse("Hey Stranger!"));
System.out.println();
```

下面是程序的输出：

```bash
First Name is set? true
First Name: Tom
Hey Tom!
```

## 九、Date/Time API

Java 8 在包`java.time`下包含了一组全新的时间日期API。新的日期API和开源的`Joda-Time`库差不多，但又不完全一样，下面的例子展示了这组新API里最重要的一些部分：

### 1. Clock 时钟

`Clock`类提供了访问当前日期和时间的方法，Clock是时区敏感的，可以用来取代`System.currentTimeMillis()`来获取当前的微秒数。某一个特定的时间点也可以使用`Instant`类来表示，`Instant`类也可以用来创建老的`java.util.Date`对象。代码如下:

```java
Clock clock = Clock.systemDefaultZone();
long millis = clock.millis();
Instant instant = clock.instant();
Date legacyDate = Date.from(instant);   // legacy java.util.Date
```

### 2. Timezones 时区

在新API中时区使用`ZoneId`来表示。时区可以很方便的使用静态方法`of`来获取到。时区定义了到UTS时间的时间差，在`Instant`时间点对象到本地日期对象之间转换的时候是极其重要的。代码如下:

```java
System.out.println(ZoneId.getAvailableZoneIds());
// prints all available timezone ids
ZoneId zone1 = ZoneId.of("Europe/Berlin");
ZoneId zone2 = ZoneId.of("Brazil/East");
System.out.println(zone1.getRules());
System.out.println(zone2.getRules());
// ZoneRules[currentStandardOffset=+01:00]
// ZoneRules[currentStandardOffset=-03:00]
```

### 3. LocalTime 本地时间

`LocalTime`定义了一个没有时区信息的时间，例如 晚上10点，或者 17:30:15。下面的例子使用前面代码创建的时区创建了两个本地时间。之后比较时间并以小时和分钟为单位计算两个时间的时间差。代码如下:

```java
LocalTime now1 = LocalTime.now(zone1);
LocalTime now2 = LocalTime.now(zone2);
System.out.println(now1.isBefore(now2));  // false
long hoursBetween = ChronoUnit.HOURS.between(now1, now2);
long minutesBetween = ChronoUnit.MINUTES.between(now1, now2);
System.out.println(hoursBetween);       // -3
System.out.println(minutesBetween);     // -239
```

`LocalTime`提供了多种工厂方法来简化对象的创建，包括解析时间字符串。代码如下:

```java
LocalTime late = LocalTime.of(23, 59, 59);
System.out.println(late);       // 23:59:59
DateTimeFormatter germanFormatter = DateTimeFormatter
        .ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(Locale.GERMAN);
LocalTime leetTime = LocalTime.parse("13:37", germanFormatter);
System.out.println(leetTime);   // 13:37
```

### 4. LocalDate 本地日期

`LocalDate`表示了一个确切的日期，比如`2014-03-11`。该对象值是不可变的，用起来和`LocalTime`基本一致。下面的例子展示了如何给`Date`对象加减天/月/年。另外要注意的是这些对象是不可变的，操作返回的总是一个新实例。代码如下:

```java
LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);
LocalDate yesterday = tomorrow.minusDays(2);
LocalDate independenceDay = LocalDate.of(2014, Month.JULY, 4);
DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();

System.out.println(dayOfWeek);    // FRIDAY
```

从字符串解析一个LocalDate类型和解析LocalTime一样简单。代码如下:

```java
DateTimeFormatter germanFormatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN);
LocalDate xmas = LocalDate.parse("24.12.2014", germanFormatter);
System.out.println(xmas);   // 2014-12-24
```

### 5. LocalDateTime 本地日期时间

`LocalDateTime`同时表示了时间和日期，相当于前两节内容合并到一个对象上了。`LocalDateTime`和`LocalTime`还有`LocalDate`一样，都是不可变的。`LocalDateTime`提供了一些能访问具体字段的方法。代码如下:

```java
LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);
DayOfWeek dayOfWeek = sylvester.getDayOfWeek();
System.out.println(dayOfWeek);      // WEDNESDAY
Month month = sylvester.getMonth();
System.out.println(month);          // DECEMBER
long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
System.out.println(minuteOfDay);    // 1439
```

只要附加上时区信息，就可以将其转换为一个时间点`Instant`对象，`Instant`时间点对象可以很容易的转换为老式的`java.util.Date`。代码如下:

```java
Instant instant = sylvester
        .atZone(ZoneId.systemDefault())
        .toInstant();
Date legacyDate = Date.from(instant);
System.out.println(legacyDate);     // Wed Dec 31 23:59:59 CET 2014
```

格式化`LocalDateTime`和格式化时间和日期一样的，除了使用预定义好的格式外，我们也可以自己定义格式。代码如下:

```java
DateTimeFormatter formatter =
    DateTimeFormatter
        .ofPattern("MMM dd, yyyy - HH:mm");
LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter);
String string = formatter.format(parsed);
System.out.println(string);     // Nov 03, 2014 - 07:13
```

和`java.text.NumberFormat`不一样的是新版的`DateTimeFormatter`是不可变的，所以它是线程安全的。

关于Java8中日期API更多的使用示例可以参考[Java 8中关于日期和时间API的20个使用示例](http://blinkfox.com/java-8zhong-guan-yu-ri-qi-he-shi-jian-apide-20ge-shi-yong-shi-li/)。

## 十、Base64

在Java 8中，Base64编码已经成为Java类库的标准。它的使用十分简单，下面让我们看一个例子：

```java
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64s {

    public static void main(String[] args) {
        final String text = "Base64 finally in Java 8!";

        final String encoded = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        System.out.println(encoded);

        final String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        System.out.println(decoded);
    }

}
```

程序在控制台上输出了编码后的字符与解码后的字符：

```bash
QmFzZTY0IGZpbmFsbHkgaW4gSmF2YSA4IQ==
Base64 finally in Java 8!
```

Base64类同时还提供了对URL、MIME友好的编码器与解码器（`Base64.getUrlEncoder() / Base64.getUrlDecoder()`, `Base64.getMimeEncoder() / Base64.getMimeDecoder()`）。

## 十一、JavaFX

`JavaFX`是一个强大的图形和多媒体处理工具包集合，它允许开发者来设计、创建、测试、调试和部署富客户端程序，并且和Java一样跨平台。从Java8开始，JavaFx已经内置到了JDK中。关于JavaFx更详细的文档可参考[JavaFX中文文档](http://www.javafxchina.net/blog/docs/)。

## 十二、其它

### 1. JDBC4.2规范

JDBC4.2主要有以下几点改动：

- 增加了对`REF Cursor`的支持
- 修改返回值大小范围（update count）
- 增加了`java.sql.DriverAction`接口
- 增加了`java.sql.SQLType`接口
- 增加了`java.sql.JDBCtype`枚举
- 对`java.time`包时间类型的支持

### 2. 更好的类型推测机制

Java 8在类型推测方面有了很大的提高。在很多情况下，编译器可以推测出确定的参数类型，这样就能使代码更整洁。让我们看一个例子：

```java
public class Value<T> {

    public static<T> T defaultValue() {
        return null;
    }

    public T getOrDefault(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }

}
```

这里是`Value<String>`类型的用法。

```java
public class TypeInference {

    public static void main(String[] args) {
        final Value<String> value = new Value<>();
        value.getOrDefault("22", Value.defaultValue());
    }

}
```

`Value.defaultValue()`的参数类型可以被推测出，所以就不必明确给出。在Java 7中，相同的例子将不会通过编译，正确的书写方式是`Value.<String>defaultValue()`。

### 3. HashMap性能提升

Java8中，HashMap内部实现又引入了红黑树，使得HashMap的总体性能相较于Java7有比较明显的提升。以下是对Hash均匀和不均匀的情况下的性能对比

#### (1). Hash较均匀的情况

![Hash较均匀时的性能对比](http://tech.meituan.com/img/java-hashmap/%E6%80%A7%E8%83%BD%E6%AF%94%E8%BE%83%E8%A1%A81.png)

#### (2). Hash极不均匀的情况

![Hash极不均匀时的性能对比](http://tech.meituan.com/img/java-hashmap/%E6%80%A7%E8%83%BD%E6%AF%94%E8%BE%83%E8%A1%A82.png)

### 4. IO/NIO 的改进

Java8 对`IO/NIO`也做了一些改进。主要包括：改进了`java.nio.charset.Charset`的实现，使编码和解码的效率得以提升，也精简了`jre/lib/charsets.jar`包；优化了`String(byte[], *)`构造方法和`String.getBytes()`方法的性能；还增加了一些新的`IO/NIO`方法，使用这些方法可以从文件或者输入流中获取流（`java.util.stream.Stream`），通过对流的操作，可以简化文本行处理、目录遍历和文件查找。

新增的 API 如下：

- `BufferedReader.line()`: 返回文本行的流`Stream<String>`
- `File.lines(Path, Charset)`: 返回文本行的流`Stream<String>`
- `File.list(Path)`: 遍历当前目录下的文件和目录
- `File.walk(Path, int, FileVisitOption)`: 遍历某一个目录下的所有文件和指定深度的子目录
- `File.find(Path, int, BiPredicate, FileVisitOption...)`: 查找相应的文件

下面就是用流式操作列出当前目录下的所有文件和目录：

```java
Files.list(new File(".").toPath()).forEach(System.out::println);
```

### 5. JavaScript引擎Nashorn

Java 8提供了一个新的`Nashorn javascript`引擎，它允许我们在JVM上运行特定的javascript应用。Nashorn javascript引擎只是`javax.script.ScriptEngine`另一个实现，而且规则也一样，允许Java和JavaScript互相操作。这里有个小例子：

```java
ScriptEngineManager manager = new ScriptEngineManager();
ScriptEngine engine = manager.getEngineByName("JavaScript");

System.out.println(engine.getClass().getName());
System.out.println("Result:" + engine.eval("function f(){return 1;}; f() + 1;"));
```

输出如下：

```bash
jdk.nashorn.api.scripting.NashornScriptEngine
Result: 2
```

### 6. 并发（Concurrency）

在新增`Stream`机制与`Lambda`的基础之上，在`java.util.concurrent.ConcurrentHashMap`中加入了一些新方法来支持聚集操作。同时也在`java.util.concurrent.ForkJoinPool`类中加入了一些新方法来支持共有资源池（common pool）（请查看我们关于Java 并发的免费课程）。

新增的`java.util.concurrent.locks.StampedLock`类提供一直基于容量的锁，这种锁有三个模型来控制读写操作（它被认为是不太有名的`java.util.concurrent.locks.ReadWriteLock`类的替代者）。

在`java.util.concurrent.atomic`包中还增加了下面这些类：

- DoubleAccumulator
- DoubleAdder
- LongAccumulator
- LongAdder

### 7. 类依赖分析器jdeps

`Jdeps`是一个功能强大的命令行工具，它可以帮我们显示出包层级或者类层级java类文件的依赖关系。它接受class文件、目录、jar文件作为输入，默认情况下，`jdeps`会输出到控制台。

作为例子，让我们看看现在很流行的Spring框架的库的依赖关系报告。为了让报告短一些，我们只分析一个jar: `org.springframework.core-3.0.5.RELEASE.jar`.

`jdeps org.springframework.core-3.0.5.RELEASE.jar`这个命令输出内容很多，我们只看其中的一部分，这些依赖关系根绝包来分组，如果依赖关系在classpath里找不到，就会显示not found.

```bash
C:\Program Files\Java\jdk1.8.0\jre\lib\rt.jar
   org.springframework.core (org.springframework.core-3.0.5.RELEASE.jar)
      -> java.io
      -> java.lang
      -> java.lang.annotation
      -> java.lang.ref
      -> java.lang.reflect
      -> java.util
      -> java.util.concurrent
      -> org.apache.commons.logging                         not found
      -> org.springframework.asm                            not found
      -> org.springframework.asm.commons                    not found
   org.springframework.core.annotation (org.springframework.core-3.0.5.RELEASE.jar)
      -> java.lang
      -> java.lang.annotation
      -> java.lang.reflect
      -> java.util
```

### 8. JVM的PermGen空间被移除

`PermGen`空间被移除了，取而代之的是`Metaspace（JEP 122）`。JVM选项`-XX:PermSize`与`-XX:MaxPermSize`分别被`-XX:MetaSpaceSize`与`-XX:MaxMetaspaceSize`所代替。

---

参考文档：

- [What's New in JDK 8](http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html)
- [Java 8新特性终极指南](http://www.importnew.com/11908.html)