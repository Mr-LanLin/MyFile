 # Java反射

---
## 一 、概述
  Java的反射机制是Java特性之一，Java的反射机制是在编译并不确定是哪个类被加载了，而是在程序运行的时候才加载、探知、自审。使用在编译期并不知道的类。这样的特点就是反射。

##  二、主要作用
- 对于编译时无法预知对象可能属于哪些类，程序只能运行时发现该对象的真实信息，就需要用到反射


##  三、具体实现（反射机制获取类的方法）

```java
/第一种方式：  
Class c1 = Class.forName("javaReflc.Demo");  
//第二种方式：  
//java中每个类型都有class 属性.  
Class c2 = Demo.class;  
   
//第三种方式：  
//java语言中任何一个java对象都有getClass 方法  
Demo e = new Demo();  
Class c3 = e.getClass(); //c3是运行时类 (e的运行时类是Employee)  

```
- 获取到类之后再创建其对象
```java

Class c1 = Class.forName("javaReflc.Demo"); 
// 调用该类的无参构造器，没有无参构造器会报java.lang.InstantiationException
Object demo = c1.newInstance();

```
- 获取所有公共方法getMethods(包括父类的) 
```java
public static void main(String[] args) {
    	 try {
             //创建类
             Class<?> class1 = Class.forName("javaReflc.Demo");

             //获取所有的公共的方法
             Method[] methods =  class1.getMethods() ;
             
             for (Method method : methods) {
                 System.out.println( method );
             }

         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         }
    }
```
一些方法的反射
- getDeclaredMethods()  获取所有的方法
- getReturnType()  获得方法的放回类型
- getParameterTypes()  获得方法的传入参数类型
- getDeclaredMethod("方法名",参数类型.class,……)  获得特定的方法

构造函数
- getDeclaredConstructors() 获取所有的构造方法
- getDeclaredConstructor(参数类型.class,……) 获取特定的构造方法

父类和接口
- getSuperclass() 获取某类的父类
- getInterfaces() 获取某类实现的接口

- **getDeclaredFields** 和 **getFields** 的区别
> getDeclaredFields()获得某个类的所有申明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
> getFields()获得某个类的所有的公共（public）的字段，包括父类。


通过有参构造器实例化类
```java
public class javaReflc {

    public static void main(String[] args) {
    	 try {
             //获得指定字符串类对象
             Class cla=Class.forName("javaReflc.Demo");
             //设置Class对象数组，用于指定构造方法类型
             Class[] cl = new Class[]{String.class};
             //获得Constructor构造器对象。并指定构造方法类型
             Constructor con = cla.getConstructor(cl);
            //给传入参数赋初值
             Object[] x={"name"};
             //得到实例
             Object obj=con.newInstance(x);
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
}

class Demo extends DemoParent{
    private String name;
    public Demo(String name) {
        super();
        this.name = name;
        System.out.println("构造成功");
    }
    
    private void Private(String a){
    	System.out.println(a);
    	
    }
    public void Public(){}

}

class DemoParent{
	
	private void parentPrivate(){}
	
	public void parentPublic(){}
	
}

```
通过setAccessible(true)给私有化成员变量赋值
```java
public class javaReflc {


    public static void main(String[] args) {
        try {
            //创建类
            Class<?> class1 = Class.forName("javaReflc.Demo");
            //创建实例
            Object demo = class1.newInstance();
            //获得id 属性
            Field idField = class1.getDeclaredField( "name" ) ;
            //setAccessible是启用和禁用访问安全检查的开关
            idField.setAccessible(true);
            //给id 属性赋值
            idField.set(demo , "100") ;
            //打印 person 的属性值
            System.out.println( idField.get( demo ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

class Demo extends DemoParent{
    private String name;
    
    private void Private(String a){
    	System.out.println(a);
    	
    }
    public void Public(){}

}

```
调用方法，并接受方法的返回值
```java
public class javaReflc {


    public static void main(String[] args) {
        try {
        	//创建类
            Class<?> class1 = Class.forName("javaReflc.Demo");
            //创建实例
            Object demo = class1.newInstance();
            //获取 getName 方法
            Method method = class1.getDeclaredMethod( "Public" ) ;
            //执行getName方法，并且接收返回值
            String name_2 = (String) method.invoke( demo  ) ;
            System.out.println( "name2: " + name_2 );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Demo extends DemoParent{
    private String name;
    
    public String Public(){
    	return "123";
    }
    
    public static void Static(){}
}
```

----
参考文档：http://www.cnblogs.com/zhaoyanjun/p/6074887.html