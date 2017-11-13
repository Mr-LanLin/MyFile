---
style: ocean
---
# Enum
======================================================================================

**枚举：在汉语中的意思是 “一一列举”，从词义也可以看出这种数据类型的特点**

    在实际编程中，往往存在着这样的“数据集”，它们的数值在程序中是稳定的，而且“数据集”中的元素是有限的。例如星期一到星期日七个数据元素组成了一周的“数据集”，春夏秋冬四个数据元素组成了四季的“数据集”

## int枚举模式
```java
//The int enum pattern - severely deficient
public static final int APPLE_FUJI = 1;
public static final int APPLE_PIPPIN = 2;
public static final int APPLE_GRANNY_SMITH = 3;

public static final int ORANGE_NAVEL = 1;
public static final int ORANGE_TEMPLE = 2;
public static final int ORANGE_BLOOD = 3;
```
    上面的常量定义方式存在诸多不便，你可以在需要APPLE_FUJI的地方使用ORANGE_NAVEL而不引起任何的编译和运行时异常，一旦常量值发生变化客户端必须重新编译，如果是混用的情况，运行时的行为即使重新编译也是无法确定的，而且往往在数据传递和使用时看到的只是个magic number，遍历int枚举常量也没有可靠的方法。

    还有String枚举模式，主要这种方式存在性能问题，因为依赖于字符串的比较操作。 但有一种方式，就比如Integer.MAX_VALUE,Integer.MIN_VALUE,MATH.PI等，分别表示整型数的最大最小值和圆周率，是和具体的类相关联一个属性或者是一个有具体意义的常量值，这样表示是合适的。

    Java枚举类型背后的基本想法非常简单：它们就是通过共有的静态final域为每个枚举常量导出实例的类。它们是单例的泛型化，本质上时单元素的枚举。所以使用枚举实现单例是一种有效方式。
  
    相对于int枚举模式，Java枚举类型有以下优势：
	 
 -  枚举类型拥有自己的命名空间，可以允许同名常量（不同命名空间）；
 -  可以增加和重排枚举类型中常量，而无需重新编译客户端；（新增常量自然无法使用）
 -  toString可以获取常量的字面值；
 -  常量可任意添加方法和字段。

    下面主要围绕文中提到的枚举常量的方法运用，进行下说明，并且发现文中提到的这些使用方式和设计模式的行为模式有些许相似，我称之为枚举的行为模式。
###  枚举常量的共同行为

    以太阳系的8大行星为例，每颗行星都有质量和半径，通过这两个属性可以计算出表面重力，从而通过物体质量可以获取在某行星上重量，例子中枚举常量两个参数分别表示行星的质量和半径：

```java
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS  (4.869e+24, 6.052e6),
    EARTH  (5.975e+24, 6.378e6),
    MARS   (6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN (5.685e+26, 6.027e7),
    URANUS (8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);
    private final double mass;           // In kilograms （质量）
    private final double radius;         // In meters （半径）
    private final double surfaceGravity; // In m / s^2 (表面重力)
 
    // Universal gravitational constant in m^3 / kg s^2  （引力常数）
    private static final double G = 6.67300E-11;
 
    // Constructor
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }
 
    public double getMass()           { return mass; }
    public double getRadius()         { return radius; }
    public double getSurfaceGravity() { return surfaceGravity; }
 
    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;  // F = ma
    }
}

public class WeightTable {
	public static void main(String[] args) {
		double earthWeight = Double.parseDouble("100");
		double mass = earthWeight / Planet.EARTH.surfaceGravity();
		for (Planet p : Planet.values())
		System.out.printf("Weight on %s is %f%n", p, p.surfaceWeight(mass));
	}
}

```
    结果如下:
			Weight on MERCURY is 37.790670
			Weight on VENUS is 90.505101
			Weight on EARTH is 100.000000
			Weight on MARS is 37.960400
			Weight on JUPITER is 252.967944
			Weight on SATURN is 106.551411
			Weight on URANUS is 90.485548
			Weight on NEPTUNE is 113.626352



    行为分析：
    每个行星都要计算其表面重量，而行星计算表面重力的公式是不变的，所以每个常量的这个行为是统一的，抽象为一个方法即可，而巧妙之处在于在初始化常量时把表面重力值一同计算出来，计算时直接取值即可。
			
### 枚举常量的不同行为

    以操作码为例，加减乘除的实现是不同的。首先是第一种实现方法：
```java
// Enum type that switches on its own value - questionable
public enum Operation {
	PLUS, MINUS, TIMES, DIVIDE;
	// Do the arithmetic op represented by this constant
	double apply(double x, double y) {
		switch(this) {
		case PLUS: return x + y;
		case MINUS: return x - y;
		case TIMES: return x * y;
		case DIVIDE: return x / y;
		}
		throw new AssertionError("Unknown op: " + this);
	}
}
```
    这段代码看似可行，但是却很脆弱，如果添加了新的常量，忘了给switch添加判断条件，编译没有问题，运行时却会报异常。而且以面向对象编程的角度来看，面对大量的switch case语句或者if else语句，那一般就是有改进的余地的。可以在枚举类型中声明一个抽象方法，在常量中进行实现，并且可以与具体的常量数值结合起来，利用toString方便的打印算术表达式，代码如下：
```java
// Enum type with constant-specific class bodies and data
public enum Operation {
	PLUS("+") {
	double apply(double x, double y) { return x + y; }
	},
	MINUS("-") {
	double apply(double x, double y) { return x - y; }
	},
	TIMES("*") {
	double apply(double x, double y) { return x * y; }
	},
	DIVIDE("/") {
	double apply(double x, double y) { return x / y; }
	};
	private final String symbol;
	Operation(String symbol) { this.symbol = symbol; }
	@Override public String toString() { return symbol; }
	abstract double apply(double x, double y);
}
public static void main(String[] args) {
	double x = 2;
	double y = 4;
	for (Operation op : Operation.values())
	System.out.printf("%f %s %f = %f%n",x, op, y, op.apply(x, y));
}
```
    这样添加新常量也不会忘记方法实现了，因为编译器会提醒.

###  基于策略的行为实现


    下面是一个可以共享代码的枚举例子：
    以工资计算为例，五个工作日，八小时之外都算加班（T_T）,算加班工资，下面是通过switch实现的一个方法：```
```java
// Enum that switches on its value to share code - questionable
enum PayrollDay {
	MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
	private static final int HOURS_PER_SHIFT = 8;
	double pay(double hoursWorked, double payRate) {
		double basePay = hoursWorked * payRate;
		double overtimePay; // Calculate overtime pay
		switch(this) {
			case SATURDAY: case SUNDAY:
				overtimePay = hoursWorked * payRate / 2;
			default: // Weekdays
				overtimePay = hoursWorked <= HOURS_PER_SHIFT ? 0 : (hoursWorked - HOURS_PER_SHIFT) * payRate / 2;
				break;
		}
		return basePay + overtimePay;
	}
}
```
    不知道看到上面代码，有没有发现问题啊，就是周末加班的代码没有加break啊（坑）。从代码维护角度看，该代码很危险，如果添加一个新的枚举值（比如病假），忘了修改switch语句，计算工资肯定出错。

    改进方式可以使用一种“策略枚举”的方式实现工资计算，代码如下：
```java
// The strategy enum pattern
enum PayrollDay {
	MONDAY(PayType.WEEKDAY), TUESDAY(PayType.WEEKDAY),
	WEDNESDAY(PayType.WEEKDAY), THURSDAY(PayType.WEEKDAY),
	FRIDAY(PayType.WEEKDAY),
	SATURDAY(PayType.WEEKEND), SUNDAY(PayType.WEEKEND);
	private final PayType payType;
	PayrollDay(PayType payType) { this.payType = payType; }
	double pay(double hoursWorked, double payRate) {
		return payType.pay(hoursWorked, payRate);
	}
	// The strategy enum type
	private enum PayType {
		WEEKDAY {
			double overtimePay(double hours, double payRate) {
			return hours <= HOURS_PER_SHIFT ? 0 : (hours - HOURS_PER_SHIFT) * payRate / 2;
			}
		},
		WEEKEND {
			double overtimePay(double hours, double payRate) {
			return hours * payRate / 2;
			}
		};
		private static final int HOURS_PER_SHIFT = 8;
		abstract double overtimePay(double hrs, double payRate);
		double pay(double hoursWorked, double payRate) {
			double basePay = hoursWorked * payRate;
			return basePay + overtimePay(hoursWorked, payRate);
		}
	}
}
```
    PayType就是策略枚举，负责工资的计算，没有了Switch，增加工作日类型，选择计算策略很容易实现代码修改。
    
    还有一种方式实现，就和策略模式一模一样了，那就是基于对枚举类的扩展。枚举的扩展是通过接口实现的。比如以工资支付为例，已经有了工作日和周末的支付方式，我们想添加国庆加班工资支付方式，接口及实现如下：
```java
//定义支付接口
public interface ShallPay {
	public double pay(double hours, double payRate);
}

//工作日周末支付实现
public enum PayType implements ShallPay {
	...
}

//国庆支付实现
public enum PayTypeHoliday  implements ShallPay {
	TRIPLE {
		public double pay(double hour, double payRate) {
			return 3 * hour * payRate;
		}
	};
}

//具体的工作时间
public enum PayrollDay {
	public enum PayrollDay {
	MONDAY(PayType.WEEKDAY), TUESDAY(PayType.WEEKDAY),
	WEDNESDAY(PayType.WEEKDAY), THURSDAY(PayType.WEEKDAY),
	FRIDAY(PayType.WEEKDAY),
	SATURDAY(PayType.WEEKEND), SUNDAY(PayType.WEEKEND),
	HOLIDAY(PayTypeHoliday.TRIPLE);
	
	private final ShallPay payType;
	PayrollDay(ShallPay payType) { this.payType = payType; }
	double pay(double hoursWorked, double payRate) {
		return payType.pay(hoursWorked, payRate);
	}	
}
```
    PayrollDay就是需要支付工资时的上下文环境，ShallPay就是策略接口，PayType，PayTypeHoliday的常量可看做具体策略的实现类。

    这种方式，在原用枚举类的基础上，扩展自己的枚举类，就可以直接添加新的枚举类型实现相同接口，使用很方便。
**注：**

    用了上述方法后switch语句是否对枚举来说没有用了呢？文中提到对外部不受控的枚举使用switch是合适的，比如Operation枚举，不受你控制，希望有个方法返回运算符的反运算，可以用下列方式：
```java
// Switch on an enum to simulate a missing method
public static Operation inverse(Operation op) {
	switch(op) {
		case PLUS: return Operation.MINUS;
		case MINUS: return Operation.PLUS;
		case TIMES: return Operation.DIVIDE;
		case DIVIDE: return Operation.TIMES;
		default: throw new AssertionError("Unknown op: " + op);
	}
}
```

##  策略模式（Strategy）的枚举（Enum）实现

    需要实现对不同类型的游戏收取不同的手续费，我们先来看一下枚举类型的实现代码，简单的有些出乎意料，我们首先定义一个枚举类型， 里面包含一个计算手续费的抽象方法，接收交易金额为参数，然后，我们枚举不同的游戏类型，端游，页游，手游，并对抽象方法进行实现，并提供一个简单的 main方法进行测试，整体代码如下：

```java
package enums;   
/**
 * 手续费计算
 *
 * Created by minghui on 2017/9/26 0026. 
 */ 
public enum HandFeeCaculator {

    //端游
  	PC {
        public double count (double amount) {
            return amount * 5 / 100;
  		}
    },
  	//页游
  	PAGE {
        public double count (double amount) {
            return amount * 2 / 100;
  		}
    },
	// 手游
  	MOBILE {
        public double count(double amount) {
            return 0.0;
  		}
    };    
	/**
 	* 手续费计算的抽象方法
	* @param amount 交易金额
    * @return
    */
	public  abstract  double count (double amount);   public static void main(String[] args) {
        //交易金额
  		double amount = 500.0;
  		//计算不同游戏类型的手续费
  		System.out.println(HandFeeCaculator.PC.count(amount));
  		System.out.println(HandFeeCaculator.PAGE.count(amount));   
        System.out.println(HandFeeCaculator.MOBILE.count(amount));
   }

}
```
    代码很简单,上面的例子中，也是对策略模式的应用，我们定义了一个抽象的count（）方法，承担Strategy的角色， 之后在枚举的每个成员中，对这个抽象方法进行了实现，承担典型的ConcreteStrategy角色，而枚举类本身又承担了Context环境角色的作 用，我们通过选择不同的枚举成员就可以达到计算不同手续费的目的。


## 下面是结合网上的一些枚举用法的总结

```java
/**
 * Created by minghui 2017/9/25 0025. */ public class TestEnum {

    /**
 * 普通枚举
  */
  public enum ColorEnum {
        red, blue, yellow, green;
  }

    /**
 * 枚举像普通的类一样可以添加属性和方法，可以为它添加静态和非静态的属性或方法
  */
  public enum SeasonEnum {
        //枚举写在最前面，否则编译出错
  		spring, summer, autumn, winter;   private final static String position = "test";   
		public  static SeasonEnum getSeason () {
	        if ("test".equals(position)) {
	            return spring;
			} else {
				return winter;
			}
     	}
    }

    /**
 	  * 性别
      *
      * 实现带有构造器的枚举
      */
  	public enum Gender {

	    //通过括号赋值，而且必须带有一个参数构造器和一个属性跟方法，否则编译出错
	  	//赋值必须都赋值或都不赋值，不能一部分赋值一部分不赋值；如果不赋值则不能写
	  	// 构造器，否则编译出错
	  	MAN ("MAN"), WOMEN ("WOMEN");   private final String value; 
	   
		//构造器默认也只能是private，从而保证构造函数只能在内部使用
		Gender (String value) {
		   this.value = value;
		}
	
	    public String getValue () {
	       return value;
	  	}

    }

    /**
 	* 订单状态
  	*
 	* 实现带有抽象方法的枚举
  	*/
  	public enum OrderState {
		  /** 已取消*/
		  CANCEL {public String getName () {return "已取消";}},
		  /** 待审核*/
		  WAITCONFIRM {public String getName () {return "待审核";}},
		  /** 等待付款*/
		  WAITPAYMENT {public String getName () {return "等待付款";}},
		  /** 正在配货*/
		  ADMEASUREPRODUCT {public String getName() {return "正在配货";}},
		  /** 等待发货 */
		  WAITDELIVER {public String getName(){return "等待发货";}},
		  /** 已发货 */
		  DELIVERED {public String getName(){return "已发货";}},
		  /** 已收货*/
		  RECEVIED {public String getName() {return "已收货"; }};  
		 
		  public abstract String getName();
  	}

    public static void main(String[] args) {
        //枚举是一种类型，用于定义变量，以限制变量的赋值；赋值时通过"枚举名.值"取得枚举中的值
  		ColorEnum colorEnum = ColorEnum.blue;
 		switch (colorEnum) {
            case red:
                 System.out.println("color is red");
				 break; 
			case green:
				 System.out.println("color is green");
				 break; 
			case blue:
				 System.out.println("color is blue");
				 break; 
			case yellow:
				 System.out.println("color is yellow");
				 break;  
		}

        //遍历枚举
  		System.out.println("遍历ColorEnum枚举中的值");
	 	for (ColorEnum color : ColorEnum.values()) {
	         System.out.println(color);
	  	}

        //获取枚举的个数
  		System.out.println("ColorEnum枚举中的值有"+ColorEnum.values().length+"个");    
		//获取枚举的索引位置，默认从0开始
		System.out.println(ColorEnum.red.ordinal());
		System.out.println(ColorEnum.green.ordinal());
		System.out.println(ColorEnum.yellow.ordinal());
		System.out.println(ColorEnum.blue.ordinal());    

		//枚举默认实现了java.lang.Compareable接口
		System.out.println(ColorEnum.red.compareTo(ColorEnum.green)); 
  		
		System.out.println("===============================");
  		System.out.println("当前季节为:" + SeasonEnum.getSeason());    

		System.out.println("=============================");
		for (Gender gender : Gender.values()) {
            System.out.println(gender);
  		}

        System.out.println("===================");
 		for (OrderState orderState : OrderState.values()) {
            System.out.println(orderState.getName());
  		}
    }
}
```


## 使用接口组织枚举

    对于enum而言，实现接口是其实现子类化的唯一办法。在一个接口的内部，创建实现该接口的枚举，以此将元素进行分组，可以达到将枚举元素分类组织的目的。如下示例，用enum来表示不同类别的食物，但同时希望每个enum元素仍然保持Food类型。

```java
package enumerated.menu;
import net.mindview.util.*;

public enum Meal2 {
	APPETIZER(Food.Appetizer.class), MAINCOURSE(Food.MainCourse.class), DESSERT(
			Food.Dessert.class), COFFEE(Food.Coffee.class);
	private Food[] values;

	private Meal2(Class<? extends Food> kind) {
		values = kind.getEnumConstants();
	}

	public interface Food {
		enum Appetizer implements Food {
			SALAD, SOUP, SPRING_ROLLS; //沙拉、汤、春卷
		}

		enum MainCourse implements Food {
			LASAGNE, BURRITO, PAD_THAI,  VINDALOO;
			//宽面条、墨西哥肉卷、泰式炒河粉、咖喱鱼
		}

		enum Dessert implements Food {
			TIRAMISU, GELATO, BLACK_FOREST_CAKE, FRUIT, CREME_CARAMEL;
			//提拉米苏、GELATO冰欺凌、黑森林蛋糕、水果、焦糖布丁
		}

		enum Coffee implements Food {
			BLACK_COFFEE, DECAF_COFFEE, ESPRESSO, LATTE, CAPPUCCINO, TEA, HERB_TEA;
		}
	}

	public Food randomSelection() {
		return Enums.random(values);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			for (Meal2 meal : Meal2.values()) {
				Food food = meal.randomSelection();
				System.out.println(food);
			}
			System.out.println("---");
		}
	}
}

```
    下面是Enums工具类，实现从枚举实例中随机选择，<T extends Enum<T>>表示T是一个枚举实例。将Class<T>作为参数，就可以利用Class对象得到enum实例的数组了。重装后的random()方法只需要使用T[]作为参数，它从数组中随机选择一个元素。

```java
//: net/mindview/util/Enums.java
package net.mindview.util;
import java.util.*;

public class Enums {
  private static Random rand = new Random(47);
  public static <T extends Enum<T>> T random(Class<T> ec) {
    return random(ec.getEnumConstants());
  }
  public static <T> T random(T[] values) {
    return values[rand.nextInt(values.length)];
  }
} ///:~

```

输出结果： 
SPRING_ROLLS 
VINDALOO 
FRUIT 
DECAF_COFFEE 
--- 
SOUP 
VINDALOO 
FRUIT 
TEA 
--- 
SALAD 
BURRITO 
FRUIT 
TEA 
--- 
SALAD 
BURRITO 
CREME_CARAMEL 
LATTE 
--- 
SOUP 
BURRITO 
TIRAMISU 
ESPRESSO 
---

    嵌入在Food中的每个enum都实现了Food接口，该接口的作用就是将其所包含的enum组合成一个公共类型，这一点是必要的。然后Meal2才能将Food中的enum作为构造器参数使用，每一个Meal2实例都将其对应的class实例对象作为构造器参数。通过getEnumConstants()方法，可以取得某个Food子类的所有enum实例。这些实例在randomSelection中被用到。因此可以从Meal2实例中随机选择一个Food,生成一份菜单。我们通过遍历每一个Meal2实例得到“枚举的枚举”的值。


## EnumMap演示命令设计模式

    EnumMap是一种特殊的Map,它要求其中的键必须来自于一个enum。枚举映射在内部表示为数组。此表示形式非常紧凑且高效。枚举映射根据其键的自然顺序来维护（该顺序是声明枚举常量的顺序）。enum的每个实例作为一个键总是存在的，但是如果你没有为这个建调用put方法来存入相应的值，其对应值就是空。EnumMap 是不同步的。如果多个线程同时访问一个枚举映射，并且至少有一个线程修改该映射，则此枚举映射在外部应该是同步的。这一般通过对自然封装该枚举映射的某个对象进行同步来完成。如果不存在这样的对象，则应该使用 Collections.synchronizedMap(java.util.Map) 方法来“包装”该枚举。最好在创建时完成这一操作，以防止意外的非同步访问： 
     Map<EnumKey, V> m 
         = Collections.synchronizedMap(new EnumMap<EnumKey, V>(...)); 
     下面例子演示了命令设计模式的用法。一般来说，命令模式首先需要一个只有单一方法的接口，然后从该接口实现具有各自不同行为的多个子类。最后，我们就可以构造命令对象，并在需要的时候使用它们。
```java
//: enumerated/EnumMaps.java
// Basics of EnumMaps.
package enumerated;

import java.util.*;
import static enumerated.AlarmPoints.*;
import static net.mindview.util.Print.*;

interface Command {
	void action();
}

public class EnumMaps {
	public static void main(String[] args) {
		EnumMap<AlarmPoints, Command> em = new EnumMap<AlarmPoints, Command>(
				AlarmPoints.class);
		em.put(KITCHEN, new Command() {
			public void action() {
				System.out.println("Kitchen fire!");
			}
		});
		em.put(BATHROOM, new Command() {
			public void action() {
				System.out.println("Bathroom alert!");
			}
		});
		for (Map.Entry<AlarmPoints, Command> e : em.entrySet()) {
			System.out.print(e.getKey() + ": ");
			e.getValue().action();
		}
		try { // If there's no value for a particular key:
			em.get(UTILITY).action();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}

//: enumerated/AlarmPoints.java
package enumerated;

public enum AlarmPoints {
	STAIR1, STAIR2, LOBBY, OFFICE1, OFFICE2, OFFICE3, OFFICE4, BATHROOM, UTILITY, KITCHEN
} // /:~

```

输出结果： 
BATHROOM: Bathroom alert! 
KITCHEN: Kitchen fire! 
java.lang.NullPointerException



