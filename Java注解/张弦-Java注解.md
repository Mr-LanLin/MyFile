## java��ע��

### ʲô��ע��

    ��JDK5��ʼ��java����Annotation����Ԫ���ݵ�֧�֣���ע��������Ϊһ���������ݵ����ݡ�

### 5��������Annotation

- @Override  ǿ��������븲�Ǹ���ķ���
- @Deprecated ��ʶĳ����򷽷��ѹ�ʱ
- @suppressWarnings ָ�����ε�Ԫ�أ�ȡ����ʾָ���ı������澯
- @SafeVarargs java7��һЩ�᷽�������𡰶���Ⱦ������ע�����ȡ������

```java
	
	public static void main(String[] args) {
	    /** java7������� ��A generic array of List<String> is created for a varargs parameter��
	        java6֮ǰ��������
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
- @FunctionalInterface  java8�ĺ���ʽ�ӿڣ��涨�ӿ�ֻ��ֻ��һ�����󷽷���


### ע��ķ���

- �����л��ƣ�ע������ڳ�����Ǹ��׶Σ���ע���Ϊ���ࣺԴ��ע��(ֻ��Դ�����)������ע��(��class�ļ���Ҳ����)������ʱע��(�����н׶���Ȼ������)

- java�Դ���ע�ⶼ�Ǳ���ʱע��

- ����Դ���ࣺ

- 1��JDK�Դ���ע�⣨JavaĿǰֻ���������ֱ�׼ע�⣺@Override��@Deprecated��@SuppressWarnings���Լ�����Ԫע�⣺@Target��@Retention��@Documented��@Inherited��
- 2����������ע�⡪����һ��ע�������ǽӴ�������������һ�ࣨsrping��@Service @Repository�ȣ�
- 3���Զ���ע�⡪��Ҳ���Կ��������Ǳ�д��ע�⣬�����Ķ������˱�дע��


### JDK��Ԫ Annotation

 @Retention ��������ע�ⶨ�壬����ָ��ע����Ա�����ʱ�䣬����һ��RetentionPllicy���͵�value��Ա����
 
 - RetentionPolicy.CLASS  ����������Annotation��¼��class�ļ��У�����ʱ���ɻ�ȡ����Ҳ��Ĭ��ֵ
 - RententionPolicy.RUNTIME ��������Annotation��¼��class�ļ��У�java��������ʱ����ͨ�������ȡ��Annotation��Ϣ
 - RetentionPolicy.SOURCE Annotationֻ������Դ�����У�������������Annotation


    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationTest{}


@Target ����ָ�������ε�Annotion������������Щ����Ԫ

- ElementType.ANNOTATION_TYPE ָ����ע��ֻ������ע��
- ElementType.CONSTRUCTOR ָ����ע��ֻ�����ι�����
- ElementType.FIELD ָ����ע��ֻ�����γ�Ա����
- ElementType.LOCAL_VARIABLE ָ����ע��ֻ�����ξֲ�����
- ElementType.METHOD ָ����ע��ֻ�����η�������
- ElementType.PACKAGE ָ����ע��ֻ�����ΰ�����
- ElementType.PARAMETER ָ����ע��������εĲ���
- ElementType.ANNOTATION_TYPE ָ����ע����������ࡢ�ӿڣ�����ע�����ͣ���ö���ඨ��


@Documented ����ָ���ñ����ε�ע����Ա�javadoc������ȡ���ĵ����������Annotation��ʱʹ����@Documented���Σ�������ʹ�ø�ע�����εĳ���Ԫ�ص�API�ĵ��ж��������Annotation��˵��

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    public @interface AnnotationTest{}


@Inherited  ָ�������ε�ע����м̳��ԡ����ĳ����ʹ���˱���ע�����ε�ע�⣬�������ཫ���м̳���

���Դ���
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

### ��ȡAnnotation��Ϣ

ʹ��ע�������ࡢ�������߳�Ա�����ȣ���Щע�ⲻ���Լ���Ч����Ҫ�������ṩ��Ӧ�Ĵ����ߡ�

java5 ��java.lang.reflect ��������AnnotatedElement�ӿڣ�Class��Constructor,Field,Method,Package ʵ���˸ýӿڣ��������ͨ�������ȡĳ�����AnnotatedElement����

AnnotationElement ������
- <T extends Annotation> T getAnnotation(Class<T> annotationClass) : ���ظó�����ڵ�ָ�������͵�ע�⣬û�з���null
- <T extends Annotation> T getDeclaredAnnotation(Class<T> annotation)java8��������
- <T extends Annotation> T[] getAnnotationsByType(Class<T> annotation)java8�����ظ�ע�⹦�ܣ�ʹ�ø÷�����ȡָ�������εĳ���Ԫ��
- <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) ͬ��


ע���һ��Ӧ��(�е�ģ��JUnit)

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
                    System.out.println("����ʧ��");
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
        System.out.println("m1����");
    }
    @AnnotationTest
    static void m2(){
        System.out.println("m2����");
    }
    
    static void m3(){
        System.out.println("m3����");
    }
    
    static void m4(){
        System.out.println("m4����");
    }
    @AnnotationTest
    static void m5(){
        System.out.println("m5����");
    }
    
}


```

�ظ�ע��ķ�����

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












