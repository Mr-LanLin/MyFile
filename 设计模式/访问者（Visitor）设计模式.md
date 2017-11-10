# 访问者（Visitor）设计模式

## 一、模式动机

对于系统中的某些对象，它们存储在同一个集合中，且**具有不同的类型**，而且对于该集合中的对象，可以接受一类称为访问者的对象来访问，而且**不同的访问者其访问方式有所不同**，访问者模式为解决这类问题而诞生

在实际使用时，对同一集合对象的操作并不是唯一的，**对相同的元素对象可能存在多种不同的操作方式**，而且这些**操作方式并不稳定，可能还需要增加新的操作**，以满足新的业务需求

访问者模式的目的是**封装一些施加于某种数据结构元素之上的操作，一旦这些操作需要修改的话，接受这个操作的数据结构可以保持不变。为不同类型的元素提供多种访问操作方式，且可以在不修改原有系统的情况下增加新的操作方式**

## 二、模式定义

访问者模式（Visitor Pattern）：表示一个**作用于某对象结构中的个元素的操作**，它使我们可以**在不改变各元素的类的前提下定义作用于这些元素的新操作**。访问者模式是一种**对象行为型**模式

## 三、模式结构

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F/%E8%AE%BF%E9%97%AE%E8%80%85.png)

访问者模式包含如下角色：

- Vistor: 抽象访问者
- ConcreteVisitor: 具体访问者
- Element: 抽象元素
- ConcreteElement: 具体元素 
- ObjectStructure: 对象结构

## 四、模式分析

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F/%E8%AE%BF%E9%97%AE%E8%80%851.png)

访问者模式中**对象结构存储了不同类型的元素对象**，以供不同访问者访问

访问者模式包含两个层次结构，一个是**访问者层次结构**，提供了抽象访问者和具体访问者，一个是**元素层次结构**，提供了抽象元素和具体元素

**相同的访问者可以以不同的方式访问不同的元素，相同的元素可以接受不同的访问者以不同访问方式访问**。在访问者模式中，增加新的访问者无需修改原有系统，系统具有较好的可扩展性

#### 典型的抽象访问者代码如下

```java
public abstract class Visitor {
	public abstract void visit(ConcreteElementA elementA);
	public abstract void visit(ConcreteElementB elementB);
	public void visit(ConcreteElementC elementC) {
		//元素ConcreteElementC操作代码
	}
} 
```

#### 典型的具体访问者代码如下

```java
public class ConcreteVisitor extends Visitor {
	public void visit(ConcreteElementA elementA) {
		//元素ConcreteElementA操作代码
	}
	public void visit(ConcreteElementB elementB) {
		//元素ConcreteElementB操作代码
	}
} 
```

#### 典型的抽象元素代码如下

```java
public interface Element {
	public void accept(Visitor visitor);
}
```

#### 典型的具体元素代码如下

```java
public class ConcreteElementA implements Element {
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	public void operationA() {
		//业务方法
	}
}
```

#### 典型的对象结构代码如下

```java
public class ObjectStructure {
	private ArrayList list=new ArrayList();
	public void accept(Visitor visitor) {
		Iterator i=list.iterator();
		while(i.hasNext()) {
			((Element)i.next()).accept(visitor);	
		}
	}
	public void addElement(Element element) {
		list.add(element);
	}
	public void removeElement(Element element) {
		list.remove(element);
	}
}
```

## 五、模式优缺点

### 优点：

- **增加新的访问操作变得很容易**
- **将有关元素对象的访问行为集中到一个访问者对象中**，而不是分散到一个个的元素类中
- 可以**跨过类的等级结构访问属于不同的等级结构的元素类**
- 让用户能够在**不修改现有类层次结构**的情况下，**定义该类层次结构的操作**

### 缺点：

- **增加新的元素类很困难**。增加一个新的元素，就需要在角色类中增加一个新的抽象操作，还需要在具体访问者中增加相应具体操作，违背了开闭原则
- **破坏封装**。访问者模式要求访问者对象访问并调用每一个元素对象的操作，这意味着元素对象有时候必须暴露一些自己的内部操作和内部状态

## 六、适用坏境

1. 一个对象结构包含很多类型的对象，**希望对这些对象实施一些依赖其具体类型的操作**。在访问者中针对每一种具体的类型都提供了一个访问操作，不同类型的对象可以有不同的访问操作
2. 需要**对一个对象结构中的对象进行很多不同的并且不相关的操作**，而需要**避免让这些操作“污染”这些对象的类，也不希望再增加新操作时修改这些类**
3. 对象结构中**对象对应的类很少改变，但经常需要在此对象结构上定义新的操作**
 
## 七、模式应用

1. 在一些**编译器的设计**中运用了访问者模式，**程序代码是被访问的对象**，它包括变量定义、变量赋值、逻辑运算等语句，编译器需要对代码进行分析，如检查变量是否定义、运算是否合法等，**可以将不同的操作封装在不同的类中**，如检查变量定义的类、检查运算是否合法的类，**这些类就是具体的访问者，可以访问程序中不同类型的语句**。除了代码分析外，还包括代码优化、空间分配和代码生成等部分，也**可以将每一个不同编译阶段的操作封装到了跟该阶段有关的访问者类中**
2. 在常用的Java XML处理技术DOM4J中，可以通过访问者模式的方式来读取并解析XML文档，VisitorSupport是DOM4J提供的Visitor接口的默认适配器，具体访问者只需继承VisitorSupport类即可

## 八、模式扩展

#### 与其他模式联用

- 由于访问者模式需要对对象结构进行操作，而对象结构本身是一个元素对象的集合，因此访问者模式经常需要与迭代器模式联用，在对象结构中使用迭代器来遍历元素对象
- 在访问这么模式中，元素对象可能存在容器对象和叶子对象，因此可以结合组合模式来进行设计

#### 倾斜的开闭原则

访问者模式，增加新的访问者方便，但是增加新的元素很困难，这是以一种倾斜的方式支持开闭原则