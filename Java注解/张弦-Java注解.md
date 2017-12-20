## java的注释

### 什么是注解

    从JDK5开始，java增加Annotation来对元数据的支持，及注解可以理解为一种描述数据的数据。

### 5个基本的Annotation

- @Override  强制子类必须覆盖父类的方法
- @Deprecated 标识某个类或方法已过时
- @suppressWarnings 指被修饰的元素，取消显示指定的编译器告警
- @SafeVarargs java7中一些会方法会引起“堆污染”，该注解可以取消警告

```java
	
	public static void main(String[] args) {
	    /** java7会给警告 “A generic array of List<String> is created for a varargs parameter”
	        java6之前不给警告
	    */
        faultyMethod(Arrays.asList("hello","123"),Arrays.asList("world"));
    }
	
	@SafeVarargs
	public static void faultyMethod(List<String> ... listStrArray ){
        List[] listArray = listStrArray;
        List<Integer> myList = new ArrayList<>();
        myList.add(123);
        listArray[1] = myList;
        System.out.println(listStrArray[0].get(1));
    }
```
- @FunctionalInterface  java8的函数式接口（规定接口只有只有一个抽象方法）


### 注解的分类

- 按运行机制（注解存在于程序的那个阶段）将注解分为三类：源码注解(只在源码存在)、编译注解(在class文件中也存在)、运行时注解(在运行阶段仍然起作用)

- java自带的注解都是编译时注解

- 按来源分类：

- 1：JDK自带的注解（Java目前只内置了三种标准注解：@Override、@Deprecated、@SuppressWarnings，以及四种元注解：@Target、@Retention、@Documented、@Inherited）
- 2：第三方的注解——这一类注解是我们接触最多和作用最大的一类（srping的@Service @Repository等）
- 3：自定义注解——也可以看作是我们编写的注解，其他的都是他人编写注解


### JDK的元 Annotation

 @Retention 用于修饰注解定义，用于指定注解可以保留的时间，包含一个RetentionPllicy类型的value成员变量
 
 - RetentionPolicy.CLASS  编译器将把Annotation记录在class文件中，运行时不可获取，这也是默认值
 - RententionPolicy.RUNTIME 编译器将Annotation记录在class文件中，java程序运行时，可通过反射获取该Annotation信息
 - RetentionPolicy.SOURCE Annotation只保留在源代码中，编译器丢弃该Annotation


    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationTest{}


@Target 用于指定被修饰的Annotion能用于修饰哪些程序单元

- ElementType.ANNOTATION_TYPE 指定该注解只能修饰注解
- ElementType.CONSTRUCTOR 指定该注解只能修饰构造器
- ElementType.FIELD 指定该注解只能修饰成员变量
- ElementType.LOCAL_VARIABLE 指定该注解只能修饰局部变量
- ElementType.METHOD 指定该注解只能修饰方法定义
- ElementType.PACKAGE 指定该注解只能修饰包定义
- ElementType.PARAMETER 指定该注解可以修饰的参数
- ElementType.ANNOTATION_TYPE 指定该注解可以修饰类、接口（包括注解类型）或枚举类定义


@Documented 用于指定该被修饰的注解可以被javadoc工具提取成文档，如果定义Annotation类时使用了@Documented修饰，则所用使用该注解修饰的程序元素的API文档中都将会包含Annotation的说明

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    public @interface AnnotationTest{}


@Inherited  指定被修饰的注解具有继承性。如果某个类使用了被该注解修饰的注解，则其子类将具有继承性

测试代码
```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface AnnotationTest{}


@AnnotationTest
public class B {

    public static void main(String[] args) {
        System.out.println(A.class.isAnnotationPresent(AnnotationTest.class));
    }


}

class A extends B{

}

```

### 提取Annotation信息

使用注解修饰类、方法或者成员变量等，这些注解不会自己生效，需要开发者提供相应的处理工具。

java5 在java.lang.reflect 包下新增AnnotatedElement接口，Class，Constructor,Field,Method,Package 实现了该接口，程序可以通过反射获取某个类的AnnotatedElement对象。

AnnotationElement 方法：
- <T extends Annotation> T getAnnotation(Class<T> annotationClass) : 返回该程序存在的指定的类型的注解，没有返回null
- <T extends Annotation> T getDeclaredAnnotation(Class<T> annotation)java8新增方法
- <T extends Annotation> T[] getAnnotationsByType(Class<T> annotation)java8中有重复注解功能，使用该方法获取指定的修饰的程序元素
- <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) 同上


注解的一个应用(有点模仿JUnit)

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface AnnotationTest {


}


public class ProcessTest {

    @SuppressWarnings("rawtypes")
    public static void process(Class clazz){
        for(Method m : clazz.getDeclaredMethods()){
            if(m.isAnnotationPresent(AnnotationTest.class)){
                try {
                    m.invoke(null);
                } catch (Exception e) {
                    System.out.println("运行失败");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ProcessTest.process(Test.class);
    }

}



public class Test {


    @AnnotationTest
    static void  m1(){
        System.out.println("m1方法");
    }
    @AnnotationTest
    static void m2(){
        System.out.println("m2方法");
    }
    
    static void m3(){
        System.out.println("m3方法");
    }
    
    static void m4(){
        System.out.println("m4方法");
    }
    @AnnotationTest
    static void m5(){
        System.out.println("m5方法");
    }
    
}


```

重复注解的方法：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface AnnotationTest {

    String name() default "123";
    int age();
    

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface AnnotationTests {


    AnnotationTest[] value();

}

@AnnotationTests(
{@AnnotationTest(name="334", age = 432),
@AnnotationTest(name="333", age = 432)})
public class Test {


}

```












