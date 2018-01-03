### jdk5 新特性

## 泛型（Generic）
C++通过模板技术可以指定集合的元素类型，而java在1.5之前一直没有相对应的功能，一个集合可以放任何类型的对象，相应地从集合里面拿对象的时候我们也不得不对他们进行强制的类型转换。jdk5引入了泛型，它允许指定集合里元素的类型，这样你可以强类型在编译时刻进行类型检查的好处。还可以引用Generic方法 

```java
public static <T> void fromArrayToCollection(T[] a, Collection<T> c) { 

       for (T o : a) { 

           c.add(o); //合法。注意与Collection<?>的区别 

    } 

} 
```


## foreach循环（Enhanced for loop）
简化集合的遍历
```java
void processAll(Collection c) {
      for(Iterator i = c.iterator();i.hasNext();) {
             MyClass myObject = (MyClass) i.next();
            myObject.process();
      }
}
 //使用foreach循环，我们可以把代码改写成：
  void processAll(Colllection<MyClass> c) {
       for(MyClass myObject : c) {
            myObject.process();
      }
 }
 //这段代码要比上面清晰许多，并且避免了强制类型转换。
```


## 自动拆装箱（Autoboxing/unboxing）

- 自动拆装箱大大方便了基本类型数据和它们包装类的使用
- 自动装箱：基本类型自动转为包装类（int >> Integer）
- 自动拆箱：包装类自动转为基本类型（Integer >> int）

- 因为自动装箱,集合可以直接add基本类型
```java
int a = 3;
Collection<Integer> c = new ArrayList<Integer>();
c.add(a);//自动转换为Integer
  
Integer b = new Integer(2);
c.add(b+2);//这里Integer先自动转换为int进行加法运算，然后int再次转换为Integer

```

## 类型安全的枚举（Type safe enums）

- JDK1.5加入了一个全新的“类”--枚举类型。为此JDK1.5引入了一个新关键字enum。

## 可变参数（Var args）

- 可变参数使程序员可以声明一个接受可变数目参数的方法。注意，可变参数必须是函数生命中的最后一个参数。加入我们要写一个简单的方法打印一些对象：
```java
util.write(obj1);
util.write(obj1,obj2);
util.write(obj1,obj2,obj3);
...
```
- 在JDK1.5之前，我们可以用重载来实现，但是这样就需要写很多的重载函数，显得不是很有效。如果使用可变参数的话，我们只需要一个函数就行了：
```java
public void write(Object... objs) {
  for(Object obj : pbjs) {
        System.out.println(obj);
  }
}
```

## 内省（Introspector）
- 通过反射的方式操作JavaBean的属性，jdk提供了PropertyDescription类来操作访问JavaBean的属性，Beantils工具基于此来实现。

```java
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;


public class Java {
    
    String name;  
    String height; 
    
    public static void main(String[] args) {
        Java jj = new Java();
        try {
            BeanInfo bi = (BeanInfo) Introspector.getBeanInfo(jj.getClass(), Object.class );
            // 调用指定的方法,没有会报错 java.beans.IntrospectionException: Method not found: isName
            PropertyDescriptor pd = new PropertyDescriptor("name2",jj.getClass());
            System.out.println(pd.getReadMethod().invoke(jj));
            // 所有的get和set方法
            PropertyDescriptor[] props = bi.getPropertyDescriptors();
            for ( int i=0;i<props.length;i++){
                // 调用set方法
                props[i].getWriteMethod().invoke(jj, "111");
                System.out.println(props[i].getName()+ "=" +
                        props[i].getReadMethod().invoke(jj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName2() {
        return name;
    }

    public void setName2(String name) {
        this.name = name;
    }

}
```

## 静态导入（Static import）
- 要使用静态成员（方法和变量）我们给出提供这个方法的类。使用静态导入可以使被导入类的所有静态变量和静态方法在当前类直接可见，使用这些静态成员无需再给出他们的类名

```java
import static java.lang.Math.*;
r = sin(PI * 2);//无需再写 r = Math.sin(Math.PI * 2);
不过，过度使用这个特性也会一定程度上降低代码的可读性。

```


## 线程池

- 固定大小的线程池

```java
import java.util.concurrent.Executors; 
import java.util.concurrent.ExecutorService; 

/** 
* Java线程：线程池- 
* 
*/ 
public class Test { 
        public static void main(String[] args) { 
                //创建一个可重用固定线程数的线程池 
                ExecutorService pool = Executors.newFixedThreadPool(2); 
                //创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口 
                Thread t1 = new MyThread(); 
                Thread t2 = new MyThread(); 
                Thread t3 = new MyThread(); 
                Thread t4 = new MyThread(); 
                Thread t5 = new MyThread(); 
                //将线程放入池中进行执行 
                pool.execute(t1); 
                pool.execute(t2); 
                pool.execute(t3); 
                pool.execute(t4); 
                pool.execute(t5); 
                //关闭线程池 
                pool.shutdown(); 
        } 
} 

class MyThread extends Thread{ 
        @Override 
        public void run() { 
                System.out.println(Thread.currentThread().getName()+"正在执行。。。"); 
        } 
}

pool-1-thread-1正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-2正在执行。。。 

```

- 单任务线程池
```java
在上例的基础上改一行创建pool对象的代码为：
//创建一个使用单个 worker 线程的 Executor，以无界队列方式来运行该线程。 
ExecutorService pool = Executors.newSingleThreadExecutor(); 

输出结果为：
pool-1-thread-1正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-1正在执行。。。 
```

- 可变尺寸的线程池
```java
与上面的类似，只是改动下pool的创建方式：
//创建一个可根据需要创建新线程的线程池，但是在以前构造的线程可用时将重用它们。 
ExecutorService pool = Executors.newCachedThreadPool(); 

pool-1-thread-5正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-4正在执行。。。 
pool-1-thread-3正在执行。。。 
pool-1-thread-2正在执行。。。 

Process finished with exit code 0
```

- 延迟连接池

```java
import java.util.concurrent.Executors; 
import java.util.concurrent.ScheduledExecutorService; 
import java.util.concurrent.TimeUnit; 

/** 
* Java线程：线程池- 
*/ 
public class Test { 
        public static void main(String[] args) { 
                //创建一个线程池，它可安排在给定延迟后运行命令或者定期地执行。 
                ScheduledExecutorService pool = Executors.newScheduledThreadPool(2); 
                //创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口 
                Thread t1 = new MyThread(); 
                Thread t2 = new MyThread(); 
                Thread t3 = new MyThread(); 
                Thread t4 = new MyThread(); 
                Thread t5 = new MyThread(); 
                //将线程放入池中进行执行 
                pool.execute(t1); 
                pool.execute(t2); 
                pool.execute(t3); 
                //使用延迟执行风格的方法 
                pool.schedule(t4, 10, TimeUnit.MILLISECONDS); 
                pool.schedule(t5, 10, TimeUnit.MILLISECONDS); 
                //关闭线程池 
                pool.shutdown(); 
        } 
}

class MyThread extends Thread { 
        @Override 
        public void run() { 
                System.out.println(Thread.currentThread().getName() + "正在执行。。。"); 
        } 
}

pool-1-thread-1正在执行。。。 
pool-1-thread-2正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-2正在执行。。。 

Process finished with exit code 0
```

- 

```java
/** 
* Java线程：线程池-自定义线程池 
* 
* @author Administrator 2009-11-4 23:30:44 
*/ 
public class Test { 
        public static void main(String[] args) { 
                //创建等待队列 
                BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<Runnable>(20); 
                //创建一个单线程执行程序，它可安排在给定延迟后运行命令或者定期地执行。 
                ThreadPoolExecutor pool = new ThreadPoolExecutor(2,3,2,TimeUnit.MILLISECONDS,bqueue); 
                //创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口 
                Thread t1 = new MyThread(); 
                Thread t2 = new MyThread(); 
                Thread t3 = new MyThread(); 
                Thread t4 = new MyThread(); 
                Thread t5 = new MyThread(); 
                Thread t6 = new MyThread(); 
                Thread t7 = new MyThread(); 
                //将线程放入池中进行执行 
                pool.execute(t1); 
                pool.execute(t2); 
                pool.execute(t3); 
                pool.execute(t4); 
                pool.execute(t5); 
                pool.execute(t6); 
                pool.execute(t7); 
                //关闭线程池 
                pool.shutdown(); 
        } 
} 

class MyThread extends Thread { 
        @Override 
        public void run() { 
                System.out.println(Thread.currentThread().getName() + "正在执行。。。"); 
                try { 
                        Thread.sleep(100L); 
                } catch (InterruptedException e) { 
                        e.printStackTrace(); 
                } 
        } 
}

pool-1-thread-1正在执行。。。 
pool-1-thread-2正在执行。。。 
pool-1-thread-2正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-2正在执行。。。 
pool-1-thread-1正在执行。。。 
pool-1-thread-2正在执行。。。 

Process finished with exit code 0
```
- 创建自定义线程池的构造方法很多，本例中参数的含义如下：
ThreadPoolExecutor
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue)
用给定的初始参数和默认的线程工厂及处理程序创建新的 ThreadPoolExecutor。使用 Executors 工厂方法之一比使用此通用构造方法方便得多。
参数：
- corePoolSize - 池中所保存的线程数，包括空闲线程。
- maximumPoolSize - 池中允许的最大线程数。
- keepAliveTime - 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
- unit - keepAliveTime 参数的时间单位。
- workQueue - 执行前用于保持任务的队列。此队列仅保持由 execute 方法提交的 Runnable 任务。
抛出：
- IllegalArgumentException - 如果 corePoolSize 或 keepAliveTime 小于零，或者 maximumPoolSize 小于或等于零，或者 corePoolSize 大于 maximumPoolSize。
NullPointerException - 如果 workQueue 为 null
- 自定义连接池稍微麻烦些，不过通过创建的ThreadPoolExecutor线程池对象，可以获取到当前线程池的尺寸、正在执行任务的线程数、工作队列等等。


#### 参考文档 http://blog.csdn.net/wlanye/article/details/51954855


