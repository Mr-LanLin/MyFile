## java 注解

注解作用：每当你创建描述符性质的类或者接口时,一旦其中包含重复性的工作，就可以考虑使用注解来简化与自动化该过程。

Java提供了四种元注解，元注解的作用就是负责注解其他注解。

Java5.0定义了4个标准的meta-annotation类型，它们被用来提供对其它 annotation类型作说明。

### Java5.0定义的元注解：
- `@Target`用于描述注解的使用范围，取值(ElementType)有：
    - CONSTRUCTOR:用于描述构造器
    - FIELD:用于描述域
    - LOCAL_VARIABLE:用于描述局部变量
    - METHOD:用于描述方法
    - PACKAGE:用于描述包
    - PARAMETER:用于描述参数
    - TYPE:用于描述类、接口(包括注解类型) 或enum声明
    
- `@Retention`表示需要在什么级别保存该注释信息，用于描述注解的生命周期,取值(RetentionPoicy)有：
    - SOURCE:在源文件中有效（即源文件保留）
    - CLASS:在class文件中有效（即class保留）
    - RUNTIME:在运行时有效（即运行时保留）

- `@Documented`用于描述其它类型的annotation应该被作为被标注的程序成员的公共API，因此可以被例如javadoc此类的工具文档化。Documented是一个标记注解，没有成员。

- `@Inherited` 元注解是一个标记注解，@Inherited阐述了某个被标注的类型是被继承的。如果一个使用了@Inherited修饰的annotation类型被用于一个class，则这个annotation将被用于该class的子类。
注意：@Inherited`只允许子类继承 实现接口继承没有用，只能是class，而且只能继承类名上面的注解

### Annotation 的优缺点 
- 优点：
    - 保存在 class 文件中，降低维护成本。
    - 无需工具支持，无需解析。
    - 编译期即可验证正确性，查错变得容易。
    - 提升开发效率。
    
- 缺点：
    - 若要对配置项进行修改，不得不修改 Java 文件，重新编译打包应用。 
    - 配置项编码在 Java 文件中，可扩展性差。
    
### xml配置文件的优缺点
- 优点：容易编辑，配置比较集中，方便修改，在大业务量的系统里面，通过xml配置会方便后人理解整个系统的架构。
- 缺点：比较繁琐，类型不安全，配置形态丑陋,配置文件过多的时候难以管理，开发效率低。

### 何时使用注解
跟代码关系紧密需要紧耦合的情况用注解，其它为松耦合配置用xml

### 自定义注解
注意：
- 成员类型是受限制的，成员只能使用八种基本数据类型以及 String、Class、Enum、Annotation 以及相应的数组型。
- 如果注解只有一个成员，则成员名必须取名为value()，在使用时可以忽略成员名和赋值号（=）。
- 注解类可以没有成员，没有成员的注解称为标识注解。
- 使用 default 关键字可以指定注解元素的默认值。注解要求定义的元素必须有确定的值，因此要么在注解的默认值中指定，要么在使用注解的时候指定。非基本数据类型的值不允许是 null。因此，经常使用空字符串(Void.class)或者0来作为默认值。也可以使用负数或空字符串来表示某个元素不存在。

通过bean生成建表语句事例
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String name() default "";
}
```

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraints {
    boolean primaryKey() default false;

    boolean notNull() default false;

    boolean unique() default false;
}
```

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlString {
    int value() default 0;

    String name() default "";

    Constraints constraints() default @Constraints;
}
```

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlInteger {
    String name() default "";

    Constraints constraints() default @Constraints;
}
```

```java
@Table(name = "t_person")
public class Person {
    @SqlString(value = 32, constraints = @Constraints(primaryKey = true))
    private String id;

    @SqlString(value = 300,constraints = @Constraints(notNull = true))
    private String name;

    @SqlString(300)
    private String sex;

    @SqlInteger
    private Integer age;
}
```

```java
public class AnnotationTest {
    
    @Test
    public void testPerson() throws Exception {
        Class<?> clazz = Class.forName("com.senlin.annotation.Person");
        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();
        Field[] fields = clazz.getDeclaredFields();

        List<String> columnList = new ArrayList();
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof SqlString) {
                    SqlString sqlString = (SqlString) annotation;
                    StringBuilder sb = new StringBuilder();
                    sb.append(field.getName())
					.append(" varchar(").append(sqlString.value()).append(") ")
					.append(getConstranints(sqlString.constraints()));
                    columnList.add(sb.toString());
                }

                if (annotation instanceof SqlInteger) {
                    SqlInteger sqlString = (SqlInteger) annotation;
                    StringBuilder sb = new StringBuilder();
                    sb.append(field.getName()).append(" int ")
					.append(getConstranints(sqlString.constraints()));
                    columnList.add(sb.toString());
                }
            }
        }

        StringBuilder sql = new StringBuilder();
        sql.append("create table ").append(tableName).append("( ");
        for (String column : columnList) {
            sql.append("\n    ").append(column).append(",");
        }

        sql.deleteCharAt(sql.length()-1);
        sql.append("\n)");
        System.out.println(sql.toString());
    }

    private String getConstranints(Constraints constraints) {
        StringBuilder result = new StringBuilder();
        if (constraints.primaryKey()) {
            result.append(" primary key");
        }

        if (constraints.notNull()) {
            result.append(" not null");
        }

        if (constraints.unique()) {
            result.append(" uniqu");
        }
        return result.toString();
    }
}
```

