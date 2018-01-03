## JDK1.5包含特性

### 1、下载地址：

http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-javase5-419410.html



### 2、自动封/拆箱

> 8个基本类型

- int --> Integer
- byte --> Byte
- short --> Short
- long --> Long
- char --> Character
- double --> Double
- float --> Float
- boolean --> Boolean

```java
Integer test = Integer.valueOf(5);
test = 6;
System.out.println(test+1);
```



### 3、泛型

- 参数化类型


- 表现形式: `public class NewInfo5<T>`
- 泛型中的标记符含义
  -  **E** - Element (在集合中使用，因为集合中存放的是元素)
  -  **T **- Type（Java 类）
  -  **K **- Key（键）
  -  **V **- Value（值）
  -  **N **- Number（数值类型）
  - **？ **-  表示不确定的java类型



### 3、[枚举](http://open.thunisoft.com/chenjiayin/probe/tree/master/%E5%AD%A6%E4%B9%A0%E5%88%86%E4%BA%AB/Java%E7%9F%A5%E8%AF%86/Java%E6%9E%9A%E4%B8%BE)

### 4、可变参数

- 表现形式：`public void test(String ... bhs)`



### 5、静态导入

- 表现形式:`import static java.util.Collections.*`
- 不同之处：
  - import后加static
  - 类后面加*
  - 引入静态成员变量和成员方法



### 6、for循环

- 增加foreach循环
- 集合/数组
- 接口：Iterable，迭代



### 7、Override可变返回类型

```java
 @Override
    public Test test() {
        return null;
    }

  public Object test(){
       return null;
    }
```



### 8、输入输出

- Scanner
- System.out.printf



### 9、增加类

- StringBuilder` StringBuilder test = new StringBuilder();`
- ConcurrentHashMap`ConcurrentHashMap<String, String> d = new ConcurrentHashMap<String, String>();`
- CopyOnWriteArrayList` CopyOnWriteArrayList<String> t = new CopyOnWriteArrayList<String>();`
- java.util.concurrent包



### 10、[注解](http://open.thunisoft.com/chenjiayin/probe/blob/master/学习分享/Java知识/Java注解)



