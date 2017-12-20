## java反射

### 什么是反射机制 

    简单的来说，反射机制指的是程序在运行时能够获取自身的信息。在java中，只要给定类的名字，那么就可以通过反射机制来获得该类的所有信息。

### 反射机制的优点与缺点 

    为什么要用反射机制？直接创建对象不就可以了吗？这就涉及到了动态与静态的概念。
    
    静态编译：在编译时确定类型，绑定对象,即通过。
    
    动态编译：运行时确定类型，绑定对象。动态编译最大限度发挥了java的灵活性，体现了多态的应用，有以降低类之间的藕合性。
    
    一句话，反射机制的优点就是可以实现动态创建对象和编译，体现出很大的灵活性，特别是在J2EE的开发中 
    它的灵活性就表现的十分明显。
    
    它的缺点是对性能有影响。使用反射基本上是一种解释操作，我们可以告诉JVM，我们希望做什么并且它 
    满足我们的要求。这类操作总是慢于只直接执行相同的操作。 
    
    反射包括了一些动态类型，所以 JVM 无法对这些代码进行优化。因此，反射操作的效率要比那些非反射操作低得多。我们应该避免在经常被执行的代码或对性能要求很高的程序中使用反射。

    由于反射允许代码执行一些在正常情况下不被允许的操作（比如访问私有的属性和方 法），所以使用反射可能会导致意料之外的副作用－－代码有功能上的错误，降低可移植性。 反射代码破坏了抽象性，因此当平台发生改变的时候，代码的行为就有可能也随着变化。

    使用反射技术要求程序必须在一个没有安全限制的环境中运行。如果一个程序必须在有安全限制的环境中运行，如 Applet，那么这就是个问题了。
    
    尽管反射非常强大，但也不能滥用。如果一个功能可以不用反射完成，那么最好就不用。
    
### 利用反射机制能获得什么信息

类中有什么信息，它就可以获得什么信息，不过前提是得知道类的名字，首先得根据传入的类的全名来创建Class对象。

    Class c=Class.forName("className");注明：className必须为全名，也就是得包含包名。
    Object obj=c.newInstance();//创建对象的实例 
    
    
获得构造函数的方法 

    Constructor getConstructor(Class[] params)//根据指定参数获得public构造器
    Constructor[] getConstructors()//获得public的所有构造器
    Constructor getDeclaredConstructor(Class[] params)//根据指定参数获得public和非public的构造器
    Constructor[] getDeclaredConstructors()//获得public和非public的所有构造器 
    
获得类方法的方法 

    Method getMethod(String name, Class[] params)//根据方法名，参数类型获得方法
    Method[] getMethods()//获得所有的public方法
    Method getDeclaredMethod(String name, Class[] params)//根据方法名和参数类型，获得public和非public的方法
    Method[] getDeclaredMethods()//获得所有的public和非public方法 
    
获得类中属性的方法 

    Field getField(String name)//根据变量名得到相应的public变量
    Field[] getFields()//获得类中所以public的方法
    Field getDeclaredField(String name)//根据方法名获得public和非public变量
    Field[] getDeclaredFields()//获得类中所有的public和非public方法 

获得父类和父接口

    getSuperclass()//获取某类的父类
    getInterfaces()//获取某类实现的接口
    
获取注解

    getAnnotation(Class<T> annotationClass)//根据注解类型获取注解
    getAnnotations()//获取所有注解
 
### 反射的基本使用

```java
public class Person implements Serializable {

    private static final long serialVersionUID = -891780556615553119L;

    private String name;

    private Integer age;

    private Date birthday;

    private Person() {
        System.out.println("non parameters constructor");
    }

    private Person(String name, Integer age, Date birthday) {
        this.name = name;
        this.age = age;
        this.birthday = birthday;
        System.out.println("parameters constructor");

    }

    private void say() {
        System.out.println("my name is " + name);
    }

    public void eat(String s) {
        System.out.println("eat " + s );
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", birthday=" + birthday +
                '}';
    }
}
```

```java
public class TestReflect {

    private static final String PERSON_CLASS = "com.senlin.test.reflect.Person";

    @Test
    public void testConstructor() {
        try {
            Class clazz = Class.forName(PERSON_CLASS);
//            为什么私有构造器访问不了
//            Constructor constructor = clazz.getDeclaredConstructor();
//            constructor.setAccessible(true);
//            Person person = (Person) clazz.newInstance();

            Constructor constructor2 = clazz.getDeclaredConstructor(String.class, Integer.class, Date.class);
            constructor2.setAccessible(true);//值为true则指示反射的对象在使用时应该取消Java语言访问检查
            Person person2 = (Person) constructor2.newInstance("gusenlin", 22, new Date());
            System.out.println(person2);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMethod(){
        try {
            Class clazz = Class.forName(PERSON_CLASS);

            Constructor constructor2 = clazz.getDeclaredConstructor(String.class, Integer.class, Date.class);
            constructor2.setAccessible(true);//值为true则指示反射的对象在使用时应该取消Java语言访问检查
            Person person2 = (Person) constructor2.newInstance("gusenlin", 22, new Date());

            Method sayMethod = clazz.getDeclaredMethod("say");
            sayMethod.setAccessible(true);
            sayMethod.invoke(person2);

            Method eatMethod = clazz.getDeclaredMethod("eat", String.class);
            eatMethod.invoke(person2,"饭");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}


```

   
### 利用反射实现简单的aop

```java
public interface IWork {

    void doWork();
}
``` 

```java
public class Student implements IWork {

    public void doWork() {
        System.out.println("student work is learning.");
    }
}

```

```java
public interface IAdvice {

    void before();

    void after();
}
```

```java
public class TransacationAdvice implements IAdvice {

    public void before() {
        System.out.println("transacation begin.");
    }

    public void after() {
        System.out.println("transacation end.");

    }
}
```

```java
public class SimpleProxy implements InvocationHandler {

    private Object obj;

    private IAdvice advice;

    public Object bind(Object obj, IAdvice advice) {
        this.obj = obj;
        this.advice = advice;
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        advice.before();
        Object result = method.invoke(obj, args);
        advice.after();
        return result;
    }
}
```

```java
public class ProxyClient {

    @Test
    public void testProxy(){
        SimpleProxy simpleProxy = new SimpleProxy();
        IWork work = (IWork) simpleProxy.bind(new Student(), new TransacationAdvice());
        work.doWork();
    }

}
```



