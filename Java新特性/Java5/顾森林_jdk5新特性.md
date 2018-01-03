## jdk5新特性汇总


### 循环

**5.0** 
```java
for (type variable : array){

   body

}

for (type variable : arrayList){

   body

}
```
**1.4** 
```java
for (int i = 0; i < array.length; i++){

   type variable = array[i];

   body
} 

for (int i = 0; i < arrayList.size(); i++){

   type variable = (type) arrayList.get(i);

   body

}
```

### 泛型

以ArrayList为例，包括创建一个容器对象和取得容器内对象操作：
**5.0**
```java
 List<String> objList = new ArrayList<String>();
arrayList.get(i)
```
**1.4**
```java
ArrayList arrayList = new ArrayList();
(Type) arrayList.get(i)

```

### 自动装箱拆箱

在JDK5.0以前，在原始类型与相应的包装类之间的转化是不能自动完成的。要完成这种转化，需要手动调用包装类的构造函数：
**5.0**

```java
Integer wrapper = n;
// 在JDK5.0环境中，可以自动转化，不再需要手工干预：
int n = wrapper;
```

**1.4**
```java
Integer wrapper = new Integer(n);
int n = wrapper.intValue();
```
### 可变参数列表
**5.0**
```java 
method(other params, p1, p2, p3)
```
**1.4**
```java 
method(other params, new Type[] { p1, p2, p3 })
```


### 可变的返回类型

在JDK5.0以前，当覆盖父类方法时，返回类型是不能改变的。现在有新的规则用于覆盖方法。如下，一个典型的例子就是clone()方法：
**5.0**
```java
public Employee clone() { ... }
...
Employee cloned = e.clone();
```
**1.4**
```java
public Object clone() { ... }
...
Employee cloned = (Employee) e.clone();
```

### 静态导入

静态导入功能对于JDK 5.0以前的版本是不支持的。
**5.0**
```java
import static java.lang.Math;
import static java.lang.System;

...
out.println(sqrt(PI));
```
**1.4**
```java
System.out.println(Math.sqrt(Math.PI));
```
### 控制台输入

JDK 5.0先前的版本没有Scanner类，只能使用JOptionPane.showInputDialog类代替。
**5.0**
```java
Scanner in = new Scanner(System.in);System.out.print(prompt);
int n = in.nextInt();
double x = in.nextDouble();
String s = in.nextLine();
```
**1.4**
 ```java
String input = JOptionPane.showInputDialog(prompt);
int n = Integer.parseInt(input);
double x = Double.parseDouble(input);
s = input;
```

### 格式化输出
JDK5.0以前的版本没有print方法，只能使用NumberFormat.getNumberInstance来代替。

**5.0**
```java
System.out.printf("%8.2f", x);
```
**1.4**
```java
NumberFormat formatter
   = NumberFormat.getNumberInstance();
formatter.setMinimumFractionDigits(2);
formatter.setMaximumFractionDigits(2);
String formatted = formatter.format(x);
for (int i = formatted.length(); i < 8; i++)
   System.out.print(" "); System.out.print(formatted);
```
### 内容面板代理

在JDK5.0先前的版本中，JFrame,JDialog,JApplet等类没有代理add和setLayout方法。

**5.0**
```java
add(component)
setLayout(manager)
```
**1.4**
```java
getContentPane().add(component)

getContentPane().setLayout(manager)
```
### StringBuilder类

JDK 5.0引入了StringBuilder类，这个类的方法不具有同步，这使得该类比StringBuffer类更高效。
**5.0**
```java
StringBuilder
``

**1.4**
```java
StringBuffer
```