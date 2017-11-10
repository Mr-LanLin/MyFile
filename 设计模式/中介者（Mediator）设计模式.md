# 中介者（Mediator）设计模式

## 一、模式动机

在面向对象的软件设计与开发过程中，“根据单一职责原则”，我们**应该尽量将对象细化，使其只负责或呈现单一的职责**。

对于一个模块，可能由很多对象构成，而这些对象之间，有直接或间接的关联关系，**为了减少对象两两之间复杂的引用关系，使之成为一个松耦合的系统，我们需要使用中介者模式**。

## 二、模式定义

中介者模式（Mediator Pattern）定义：用一个中介对象来**封装一系列的对象交互**，中介者使各对象不需要现实地相互引用，从而**降低其耦合**，而且可以**独立地改变他们之间的交互**。中介者模式又称为**调停者模式**，它是一种**对象行为型模式**。

## 三、模式结构

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F/%E4%B8%AD%E4%BB%8B%E8%80%85%E7%B1%BB%E5%9B%BE.png)

中介者模式包含以下角色：

- AbstractMediator：抽象中介者
- ConcreteMediator：具体中介者
- AbstractColleague：抽象同事类
- ConcreteColleague：具体同事类

## 四、模式分析

![image](https://raw.githubusercontent.com/Mr-LanLin/MyFile/master/%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F/%E4%B8%AD%E4%BB%8B%E8%80%85%E5%9B%BE.png)

以找房子为例，在没有中介机构以前，要找房子只能**询问找过房子的同事**，然后去对应小区**找房东咨询相关信息**，如果此房东没有你需要的房屋，可能会**把你推荐给其他房东**。这其中会有很多问题，同事找的小区类型不是你想要的，房屋类型不是你想要的，以及房东不在等等...有了中介机构一切都变得简单了，比如58某城，你想要什么类型的小区、什么类型的房屋，一搜索就出来了，甚至还可以在评论里找人聊聊看房情况。

中介者承担两方面职责：

- **中转作用（结构性）**：通过中介者提供的中转作用，各个同事对象就不再需要显示引用其他同事，当需要和其他同事进行通信时，通过中介者即可。该中转作用属于中介者**在结构上的支持**。
- **协调作用（行为性）**：中介者可以更进一步的对同事之间的关系进行封装，同事可以一致地和中介者进行交互，而不需要指明中介者需要具体怎么做，中介者根据封装在自身内部的协调逻辑，对同事的请求进一步处理，将同事成员之间的关系行为进行分离和封装。该协调作用属于中介者**在行为上的支持**。

#### 典型的抽象中介者代码如下

```java
public abstract class Mediator {
	protected ArrayList colleagues;
	public void register(Colleague colleague) {
		colleagues.add(colleague);
	}
	public abstract void operation();
}
```

#### 典型的具体中介者代码如下：

```java
public class ConcreteMediator extends Mediator {
	public void operation() {
		......
		((Colleague)(colleagues.get(0))).method1();
		......
	}
}
```

#### 典型的具体抽象同事代码如下：

```java
public abstract class Colleague {
	protected Mediator mediator;
	public Colleague(Mediator mediator) {
		this.mediator=mediator;
	}
	public abstract void method1();
	public abstract void method2();
}
```

#### 典型的具体同事代码如下：

```java
public class ConcreteColleague extends Colleague {
	public ConcreteColleague(Mediator mediator) {
		super(mediator);
	}
	public void method1() {
		......
	}
	public void method2() {
		mediator.operation1();
	}
} 
```
 
## 五、模式的优缺点

### 优点

- **简化了对象之间的交互**
- **将各同事解耦**
- **减少子类生成**
- **简化哥同事类的设计和实现**

### 缺点

- 在具体中介者类中包含了同事之间的交互细节，可能会导致**具体中介者类非常复杂，使得系统难以维护**
 
## 六、适用坏境

1. 系统中**对象之间存在复杂的引用关系**，产生的相互依赖关系结构混乱且难以理解
2. 一个对象由于引用了其他很多对象并且直接和这些对象通信，导致**难以复用该对象**
3. **相通过一个中间类来封装多个类中的行为，而又不想生成太多的子类**

## 七、模式的应用

1. 中介者模式在**事件驱动类软件**中应用比较多，在设计GUI应用程序时，组件之间可能**存在较为复杂的交互关系**，一个组件的改变将影响与之相关的其他组件，此时可以**使用中介者模式来对组件进行协调**，将**交互的组件作为具体的同事类**，将他们之间的**引用和控制关系交由中介者负责**
2. MVC是Java EE的一个基本模式，此时**控制器Controller作为一种中介者**，它负责控制视图对象View和模型对象Model的交互。如在Struts中，Action就可以作为JSP页面与业务对象之间的中介者
3. JDK中的javax.swing.ButtonGroup，**通过ButtonGroup来协调多个AbstractButton**。`protected Vector<AbstractButton> buttons = new Vector();`
4. JDK中的java.util.Timer，**协调TimerTask**。`private TaskQueue queue = new TaskQueue();private TimerThread thread = new TimerThread(queue);`

## 八、模式扩展

中介者模式中，通过创造出一个中介者对象，将系统中有关的对象所引用的其他对象数目减少到最少，使得一个对象与其他同事之间的相互作用被这个对象与中介者对象间的相互作用所取代。因此，中介者模式就是迪米特法则的一个典型应用。