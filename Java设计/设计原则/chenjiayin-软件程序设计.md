# 软件程序设计

标签：Java 设计原则

- 前言
- 通用设计原则
- 面向对象设计原则
- 设计模式
- 重构
- 重构示例

---

## 前言

软件也像人一样，具有生命力，从出生到死亡，会经历多种变化。软件架构设计也不是一蹴而就的，是不断地演进发展。但为了能较好的发展，在软件设计时需要考虑一些原则。

软件设计原则是一组帮助我们避开不良设计的指导方针。这些设计原则是由`Robert Martin`在《敏捷软件开发：原则、模式与实践》一书中整理收集而来。根据`Robert Martin`的理论，应该避免不良设计的以下三个重要特点：

- **僵化**：很难做改动，因为每一个细微的改动都会影响到系统大量的其他功能
- **脆弱**：每当你做一次改动，总会引起系统中预期之外的部分出现故障
- **死板**：代码很难在其他应用中重用，因其不能从当前应用中单独抽离出来

## 通用设计原则

### KISS

所谓`KISS`原则，即：`Keep It Simple,Stupid`，指设计时要坚持简约原则，避免不必要的复杂化，并且易于修改。。

> Everything should be made as simple as possible, but not simpler. - Albert Einstein

简单清晰、功能强大是软件设计最重要的原则和目标。但是软件工程天然错综复杂，而“简单”却没有一个衡量标准，判断和实现一个东西是不是简单，可以通过以下方式来参考。

- 让别的软件工程师以一种最容易的方式使用你的方案。
- 简单不是走捷径，不是为手边的问题找一个最快的方案。
- 当系统变得更庞大更复杂的时候依然能够被理解。
- 如果系统无法保持简单，那么我们能做的就是保持各个局部简单，即任何单个的类、模块、应用的设计目标及工作原理都能被快速理解。

> **我的理解**：保持简单但不能掩盖软件丰富的内涵。即**简约而不简单**！简约是对复杂的事物抽丝剥茧去除细枝末节显露主要逻辑的过程。就像小时候老师教写文章，要求尽可能用朴实的语言，言简意赅的写出来，但却又要避免语言过于贫乏。软件的“抽象”和它的“直观性”，其实是一对矛盾的关系，软件设计就要保证这两者的平衡。代码抽象过于复杂会陷入“过渡设计”不易理解的困境；为了“直观性”缺乏抽象，长此以往又会出现大量的重复、不易于扩展和难维护的困境。

### DRY

所谓`DRY`原则，即：`Don't Repeat Yourself`，不要让自己重复。

重复代码是软件程序变烂的万恶之首。`DRY`并不是指你不能复制代码，而是你复制的代码不能包含重复的“信息”。复制的东西并不仅仅是复制了代码，而是由于你把同一个信息散播在了代码的各个部分导致了有很多相近的代码也散播在各个地方。代码之所以要写的好，不要重复某些“信息”，因为需求人员总是要改需求，不改代码你就要死，改代码你就要加班，所以为了减少修改代码的痛苦，我们不能重复任何信息。举个例子，有一天需求人员说，要把分隔符从分号改成空格！一下子就要多个地方了。

所以，**去掉重复的信息会让你的代码结构发生本质的变化**。

“重复代码”有很多变体：

- 魔法数字、魔法字符串等
- 相同或相似的代码块
- 相似的逻辑及操作

对于消除重复的代码有**事不过三**法则。

- 第一次先写了一段代码。
- 第二次在另一个地方写了一段相同或相似的代码，你已经有消除和提取重复代码的冲动了。
- 再次在另一个地方写了同样的代码，你已忍无可忍，现在可以考虑提取和消除重复代码了。

> **我的理解**：解决重复的最佳的方式是通过培养良好的编码习惯来避免重复，通过**重构**的手段来消除重复。发现和解决重复并不困难，通过提取抽象、提取方法等措施就能消除重复，但困难的是立即行动去解决重复，从而不断的磨砺和提升自己的编程技艺，不断将私人代码变成公共代码，这才是自我提升的过程。解决了重复，经过一段时间，你就发现对整个系统的理解程度，在不知不觉中提高了不少。

### Maximize Cohesion,Minimize Coupling

所谓`Maximize Cohesion,Minimize Coupling`原则，即：高内聚低耦合。这是判断设计好坏的标准，主要是看模块内的内聚性是否高，模块间的耦合度是否低。

- 耦合性：也称块间联系。指软件系统结构中各模块间相互联系紧密程度的一种度量。模块之间联系越紧密，其耦合性就越强，模块的独立性则越差。模块间耦合高低取决于模块间接口的复杂性、调用的方式及传递的信息。耦合是软件结构中各模块之间相互连接的一种度量，耦合强弱取决于模块间接口的复杂程度、进入或访问一个模块的点以及通过接口的数据。
- 内聚性：又称块内联系。指模块的功能强度的度量，即一个模块内部各个元素彼此结合的紧密程度的度量。若一个模块内各元素（语名之间、程序段之间）联系的越紧密，则它的内聚性就越高。内聚是从功能角度来度量模块内的联系，一个好的内聚模块应当恰好做一件事。它描述的是模块内的功能联系。

内聚和耦合是密切相关的，同其他模块存在高耦合的模块意味着低内聚，而高内聚的模块意味着该模块同其他模块之间是低耦合。在进行软件设计时，应力争做到高内聚，低耦合。

Java中实现高内聚低耦合的常用方式：

- 少使用类的继承，多用接口隐藏实现的细节。
- 模块的功能化分尽可能的单一，道理也很简单，功能单一的模块供其它模块调用的机会就少。 
- 遵循一个定义只在一个地方出现。 
- 少使用全局变量。 
- 类属性和方法的声明少用public，多用private关键字，
- 多用设计模式，比如采用MVC的设计模式就可以降低界面与业务逻辑的耦合度。
- 尽量不用“硬编码”的方式写程序，同时也尽量避免直接用SQL语句操作数据库。
- 最后当然就是避免直接操作或调用其它模块或类（内容耦合）。

### YAGNI

所谓`YAGNI`原则，即：`You Ain’t Gonna Need It`，你不需要它。它是一种极限编程（XP）实践，表示程序员不应为目前还不需要的功能编写代码。`YAGNI`很像`KISS`原则，因为它也是致力于构建简单的方案。然而，`KISS`是通过尽可能容易的完成某件事情来实现精简方案；但`YAGNI`是通过根本就不实现它来达到精简。`YAGNI`的观点是你应该**为了眼前的需要做设计而不是未来**。

> 只在真正需要某些功能的时候才去实现它，而不是仅仅因为你预见到它将出现。- XP的联合创始人Ron Jeffries

即使你非常确信将来你需要某个特性，也不要现在就去实现它。在很多情况下，你会发现或许最终你不需要它了，或者是你真正所需的特性与你之前预计的有很大的出入。遵循YAGNI实践有两个主要原因：

- 你节约了时间，因为你避免了编写最终证明不必要的代码。
- 你的代码质量更高了，因为你使代码不必为你的“推测”所污染，而这些“推测”最终可能或多或少有些错误，但此时这些错误已牢牢地依附在你的代码中了。

> **我的理解**：只有当你真正需要的时候才去添加额外的功能，不需要就不要画蛇添足。同时对于没有被使用到的代码，都应该立即删除，从而保持系统的精简，如果将来需要时再去书写或恢复，而且那时侯写出的代码也绝对比以前的代码好。

## 面向对象设计原则

### SRP

所谓`SRP`原则，即：`Single Responsibility Principle`，单一职责原则。原始定义如下：

> There should never be more than one reason for a class to change.(只有一个引起类改变的原因)

在面向对象编程领域中，单一职责原则（`Single responsibility principle`）规定每个类都应该有一个单一的职责或者叫功能，并且该功能应该由这个类完全封装起来。所有它的（这个类的）服务都应该严密的和该功能平行（功能平行，意味着没有依赖）。一个类或者模块应该有且只有一个改变的原因。

想象有一个用于编辑和打印报表的模块。这样的一个模块存在两个改变的原因。第一，报表的内容可以改变（编辑）。第二，报表的格式可以改变（打印）。这两方面会的改变因为完全不同的起因而发生：一个是本质的修改，一个是表面的修改。单一职责原则认为这两方面的问题事实上是两个分离的功能，因此他们应该分离在不同的类或者模块里。把有不同的改变原因的事物耦合在一起的设计是糟糕的。保持一个类专注于单一功能点上的一个重要的原因是，它会使得类更加的健壮。

单一职责的好处：

- 类的复杂性降低，实现什么职责都有清晰明确的定义;
- 可读性提高，复杂性降低，可维护性提高;
- 变更引起的风险降低。

### LSP

所谓`LSP`原则，即：`Liskov Substitution principle`，里氏替换原则。原始定义如下：

> Functions that use pointers of references to base classes must be able to use objects of derived classes without knowing it.（所有引用基类的地方必须能透明地使用其子类的对象）

更通俗的定义即为：**子类可以扩展父类的功能，但不能改变父类原有的功能**。里氏替换原则包含了一下4层含义：

- 子类必须完全实现父类的方法。在类中调用其他类是务必要使用父类或接口，如果不能使用父类或接口，则说明类的设计已经违背了LSP原则。
- 子类可以有自己的个性。子类当然可以有自己的行为和外观了，也就是方法和属性。
- 覆盖或实现父类的方法时输入参数可以被放大。即子类可以重载父类的方法，但输入参数应比父类方法中的大，这样在子类代替父类的时候，调用的仍然是父类的方法。即以子类中方法的前置条件必须与超类中被覆盖的方法的前置条件相同或者更宽松。
- 覆盖或实现父类的方法时输出结果可以被缩小。

### ISP

所谓`ISP`原则，即：`Interface Segregation Principle`，接口隔离原则。原始定义如下：

> Clients should not be forced to depend upon interfaces that they do not use.(客户端只依赖于它所需要的接口；它需要什么接口就提供什么接口，把不需要的接口剔除掉。)

> The dependency of one class to another one should depend on the smallest possible interface.(类间的依赖关系应建立在最小的接口上。)

即，**接口尽量细化，接口中的方法尽量少**。接口隔离原则与单一职责原则的审视角度是不同的，单一职责原则要求的是类和接口职责单一，注重的是职责，这是业务逻辑上的划分，而接口隔离原则要求接口的方法尽量少。根据接口隔离原则拆分接口时，首先必须满足单一职责原则。

采用接口隔离原则对接口进行约束时，要注意以下几点：

- 接口尽量小，但是要有限度。对接口进行细化可以提高程序设计灵活性是不挣的事实，但是如果过小，则会造成接口数量过多，使设计复杂化。所以一定要适度。
- 为依赖接口的类定制服务，只暴露给调用的类它需要的方法，它不需要的方法则隐藏起来。只有专注地为一个模块提供定制服务，才能建立最小的依赖关系。
- 提高内聚，减少对外交互。使接口用最少的方法去完成最多的事情。

运用接口隔离原则，一定要适度，接口设计的过大或过小都不好。设计接口的时候，只有多花些时间去思考和筹划，才能准确地实践这一原则。

### OCP

所谓`OCP`原则，即：`Open Closed Principle`，开闭原则。原始定义如下：

> software entities (classes, modules, functions, etc.) should be open for extension, but closed for modification.(对扩展开放，对修改关闭)

开闭原则（OCP）是面向对象设计中“可复用设计”的基石，是面向对象设计中最重要的原则之一，其它很多的设计原则都是实现开闭原则的一种手段。核心就是：**对扩展开放，对修改关闭**。

实现开闭原则的关键就在于“抽象”。把系统的所有可能的行为抽象成一个抽象底层，这个抽象底层规定出所有的具体实现必须提供的方法的特征。作为系统设计的抽象层，要预见所有可能的扩展，从而使得在任何扩展情况下，系统的抽象底层不需修改；同时，由于可以从抽象底层导出一个或多个新的具体实现，可以改变系统的行为，因此系统设计对扩展是开放的。在实际开发过程的设计开始阶段，就要罗列出来系统所有可能的行为，并把这些行为加入到抽象底层，根本就是不可能的，这么去做也是不经济的。因此我们应该现实的接受修改拥抱变化，使我们的代码可以对扩展开放，对修改关闭。

开闭原则的好处：

- 可复用性好;
- 可维护性好。

### DIP

所谓`DIP`原则，即：`Dependency Inversion Principle`，依赖倒置原则。原始定义如下：

> High-level modules should not depend on low-level modules. Both should depend on abstractions.(高层模块不应该依赖低层模块，两者都应该依赖其抽象)

> Abstractions should not depend on details. Details should depend on abstractions.(抽象不应该依赖细节；细节应该依赖抽象)

面向过程的开发，上层调用下层，上层依赖于下层，当下层剧烈变动时上层也要跟着变动，这就会导致模块的复用性降低而且大大提高了开发的成本。面向对象的开发很好的解决了这个问题，一般情况下抽象的变化概率很小，让用户程序依赖于抽象，实现的细节也依赖于抽象。即使实现细节不断变动，只要抽象不变，客户程序就不需要变化。这大大降低了客户程序与实现细节的耦合度。

依赖倒置原则主要有以下三层含义： 

- 高层模块不应该依赖低层模块，两者都应该依赖其抽象（抽象类或接口）；
- 抽象不应该依赖细节（具体实现）；  
- 细节（具体实现）应该依赖抽象。

依赖倒置原则基于这样一个事实：**相对于细节的多变性，抽象的东西要稳定的多**。以抽象为基础搭建起来的架构比以细节为基础搭建起来的架构要稳定的多。在Java中，抽象指的是接口或者抽象类，细节就是具体的实现类，使用接口或者抽象类的目的是制定好规范和契约，而不去涉及任何具体的操作，把展现细节的任务交给他们的实现类去完成。**依赖倒置原则的核心思想就是面向接口编程**。

### LOD | LKP

所谓`LOD`原则，即：`Law of Demeter`，迪米特法则，又叫作最少知识原则（`Least Knowledge Principle`，简写`LKP`），就是说一个对象应当对其他对象有尽可能少的了解。通俗的讲，一个类应该对自己需要耦合或调用的类知道得最少，被耦合的类是如何的复杂都和我没关系，即为“不和陌生人说话”。迪米特法则的英文解释如下：

> talk only to your immediate friends.(只与直接的朋友通信)

迪米特法则的初衷在于降低类之间的耦合。由于每个类尽量减少对其他类的依赖，因此，很容易使得系统的功能模块功能独立，相互之间不存在（或很少有）依赖关系。

迪米特法则不希望类之间建立直接的联系。如果真的有需要建立联系，也希望能通过它的友元类来转达。因此，应用迪米特法则有可能造成的一个后果就是：系统中存在大量的中介类，这些类之所以存在完全是为了传递类之间的相互调用关系——这在一定程度上增加了系统的复杂度,同时也为系统的维护带来了难度。所以，在采用迪米特法则时需要反复权衡，不遵循不对，严格执行又会“过犹不及”。既要做到让结构清晰，又要做到高内聚低耦合。

### CRP

所谓`CRP`原则，即：`Composite Reuse Principle`，组合复用原则。

组合复用原则的核心思想是：**尽量使用对象组合，而不是继承来达到复用的目的**。该原则就是在一个新的对象里面使用一些已有的对象，使之成为新对象的一部分：新的对象通过向这些对象的委派达到复用已有功能的目的。

继承的缺点主要有以下几点：

- 继承复用破坏数据封装性，将基类的实现细节全部暴露给了派生类，基类的内部细节常常对派生类是透明的，白箱复用。虽然简单，但不安全，不能在程序的运行过程中随便改变。
- 基类的实现发生了改变，派生类的实现也不得不改变。
- 从基类继承而来的派生类是静态的，不可能在运行时间内发生改变，因此没有足够的灵活性。

由于组合可以将已有的对象纳入到新对象中，使之成为新对象的一部分，因此新对象可以调用已有对象的功能，这样做有下面的好处：

- 新对象存取`组成对象`的唯一方法是通过`组成对象`的`getter/setter`方法。
- 组合复用是黑箱复用，因为组成对象的内部细节是新对象所看不见的。
- 组合复用所需要的依赖较少。
- 每一个新的类可以将焦点集中到一个任务上。
- 组合复用可以在运行时间动态进行，新对象可以动态的引用与成分对象类型相同的对象。

组合复用的缺点：就是用组合复用建造的系统会有较多的对象需要管理。

组合复用原则可以使系统更加灵活，类与类之间的耦合度降低，一个类的变化对其他类造成的影响相对较少，因此一般首选使用组合来实现复用；其次才考虑继承。在使用继承时，需要严格遵循里氏代换原则，有效使用继承会有助于对问题的理解，降低复杂度，而滥用继承反而会增加系统构建和维护的难度以及系统的复杂度，因此需要慎重使用继承复用。

使用继承时必须满足`Is-A`的关系是才能使用继承，而组合却是一种`Has-A`的关系。导致错误的使用继承而不是使用组合的一个重要原因可能就是错误的把`Has-A`当成了`Is-A`。

## 设计模式

设计模式是人们基于以上诸多设计原则，在面对同类型软件工程设计问题所总结出的一些有用经验。模式不是代码，而是某类问题的通用设计解决方案。

### 设计模式分类

`GOF`设计模式共23种，分为三种类型：

- 创建型模式：单例模式、抽象工厂模式、工厂方法模式、建造者模式、原型模式。
- 结构型模式：适配器模式、桥接模式、装饰模式、组合模式、外观模式、享元模式、代理模式。
- 行为型模式：模版方法模式、命令模式、迭代器模式、观察者模式、中介者模式、备忘录模式、解释器模式、状态模式、策略模式、职责链模式、访问者模式。

### 设计模式简介

- `Singleton`（单例模式）：保证一个系统仅有一个实例，并提供一个访问它的全局访问点。
- `Abstract Factory`（抽象工厂模式）：提供一个创建一系列相关或相互依赖对象的接口，而无需指定它们具体的类。
- `Factory Method`（工厂方法模式）：定义一个用于创建对象的接口，让子类决定将哪一个类实例化。`Factory Method`使一个类的实例化延迟到其子类。
- `Builder`（建造者模式）：将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示。
- `Prototype`（原型模式）：用原型实例指定创建对象的种类，并且通过拷贝这个原型来创建新的对象。
- `Adapter`（适配器模式）：将一个类的接口转换成客户希望的另外一个接口。Adapter模式使得原本由于接口不兼容而不能一起工作的那些类可以一起工作。
- `Bridge`（桥接模式）：将抽象部分与它的实现部分分离，使它们都可以独立地变化。
- `Decorator`（装饰模式）：动态地给一个对象添加一些额外的职责。就扩展功能而言， 它比生成子类方式更为灵活。
- `Composite`（组合模式）：将对象组合成树形结构以表示“部分-整体”的层次结构。它使得客户对单个对象和复合对象的使用具有一致性。
- `Facade`（外观模式）：为子系统中的一组接口提供一个一致的界面，Facade模式定义了一个高层接口，这个接口使得这一子系统更加容易使用。
- `Flyweight`（享元模式）：运用共享技术有效地支持大量细粒度的对象。
- `Proxy`（代理模式）：为其他对象提供一个代理以控制对这个对象的访问。
- `Template Method`（模板方法模式）：定义一个操作中的算法的骨架，而将一些步骤延迟到子类中。`Template Method`使得子类可以不改变一个算法的结构即可重定义该算法的某些特定步骤。
- `Command`（命令模式）：将一个请求封装为一个对象，从而使你可用不同的请求对客户进行参数化；对请求排队或记录请求日志，以及支持可取消的操作。
- `Iterator`（迭代器模式）：提供一种方法顺序访问一个聚合对象中各个元素，而又不需暴露该对象的内部表示。
- `Observer`（观察者模式）：定义对象间的一种一对多的依赖关系,以便当一个对象的状态发生改变时,所有依赖于它的对象都得到通知并自动刷新。
- `Mediator`（中介模式）：用一个中介对象来封装一系列的对象交互。中介者使各对象不需要显式地相互引用，从而使其耦合松散，而且可以独立地改变它们之间的交互。
- `Memento`（备忘录模式）：在不破坏封装性的前提下，捕获一个对象的内部状态，并在该对象之外保存这个状态。这样以后就可将该对象恢复到保存的状态。
- `Interpreter`（解析器模式）：给定一个语言, 定义它的文法的一种表示，并定义一个解释器, 该解释器使用该表示来解释语言中的句子。
- `State`（状态模式）：允许一个对象在其内部状态改变时改变它的行为。对象看起来似乎修改了它所属的类。
- `Strategy`（策略模式）：定义一系列的算法,把它们一个个封装起来, 并且使它们可相互替换。本模式使得算法的变化可独立于使用它的客户。
- `Chain of Responsibility`（职责链模式）：为解除请求的发送者和接收者之间耦合，而使多个对象都有机会处理这个请求。将这些对象连成一条链，并沿着这条链传递该请求，直到有一个对象处理它。
- `Visitor`（访问者模式）：表示一个作用于某对象结构中的各元素的操作。它使你可以在不改变各元素的类的前提下定义作用于这些元素的新操作。

## 重构

### 重构概述

在没有重构这个技术之前，广泛采用的是`Big Front Design`，在开始编码之前要进行非常详细的设计，考虑应对未来出现的各种变化，项目一旦进入编码阶段，工作主要就是机械的把设计转换成可执行语句，而这种态度是许多程序低效、不可维护的最大原因。软件的复杂性来自于大量的不确定性，而这个不确定性上是无法避免的。需求在变，语言在变，框架在变，工具在变，架构在变，趋势在变，甚至连组织结构都在不断的变化。随着变化的不断产生，软件变得越来越复杂。

而有了重构技术之后，前期设计的压力就小了，毕竟可以随时通过重构来改善设计，应对变化。重构教会了我如何通过高效安全地改善内部设计以使之适应外部的不确定性和频繁变化。所以你大可不必一上来就应用《设计模式》把代码搞复杂，先用简单的实现满足当前需求即可。等变化真正来临时，再通过重构技术调整设计，模式给我们提供了一个方向，但并不是最终目标。只要最终的代码符合好的原则，干净整洁没有坏味道，管它符不符合某个模式呢？

### 重构的理解

![重构][1]

对于什么是重构，《重构-改善既有代码的设计》一书中已经有明确的定义，分名词和动词两种形式。

- 重构（名词）：对软件内部结构的一种调整，目的是在不改变软件可观察行为的前提下，提高其可理解性，降低其修改成本。
- 重构（动词）：使用一系列重构手法，在不改变软件可观察行为的前提下，调整其结构。

重构就是为了让软件始终可以维护，保证开发效率，一种以可控的方式整理代码的技术，在不改变软件可观察行为的前提下改善其内部结构，重构也是基于以上诸多设计原则而逐步改善代码、优化代码的过程。重构主要有以下好处：

- 重构可以改进软件设计。软件开发中唯一保持不变的就是变化。当软件因为需求变更而开始逐渐退化时，运用软件重构改善我们的结构，使之重新适应软件需求的变化。经常性的重构可以维护代码原有的形态。
- 重构可以帮助理解代码。在理解代码时，尝试去按自己的理解修改，使代码更趋简洁，随之而来的是看到一些以前看不到的设计层面的东西。

### 重构技巧

基于以上的原则思路把重构抽取出十六个字，即所谓的：**重构十六字心法**。

![重构十六字心法][2]

常见的重构技巧如下：

- 静态检查。现在市面上有很多代码静态检查的工具，也是发现bug和风格不好的比较容易的方式。解决Sonar质量管理平台上的所有问题，避免技术债务。
- 删除冗余代码。某个函数、类、变量如果不再工作，就应该将其删除。过多的冗余代码将会增加他人理解代码的难度，且使编译后的目标文件增大。局部变量应在使用前定义，没必要在函数入口处一次定义。
- 遵循好的代码风格。重构后的代码应尽量与好的代码风格保持一致。原来不好的风格，如变量跟运算符间未添加空格、函数参数之间空格、函数之间未空出一行等等，应进行修改。
- 重命名。糊不清的方法名会影响代码的可用性。这些模糊不清的名称应该重命名为有意义且与业务有关的名称，来帮助更好地理解代码。对类，接口，方法，变量、参数等重命名，以使得更易理解。名称应该能比较清晰的说明该函数、变量、类的职责。好的名称可以达到自注释的目的。
- 消除魔法数字魔法常量。应该使用常量替代魔法数字和常量。这能大大增强代码可维护性、可读性。
- 减少函数参数。如果一个函数或方法的参数过多就会出现如果更改了其中一个参数， 就得在多个调用点进行更改。可以将多个参数封装成一个结构体或类。
- 过大的类拆分成多个类。将一个类承担的多个职责拆分成多个类，以使每个类职责相对单一。
- 将过长的方法分解成多个功能相对单一、命名良好的小方法。更容易理解且具有更好的复用性。很多程序员担心带来性能损耗，拆分后的多个小函数调用的性能消耗微乎其微。与其带来的好处相比可以忽略。如果确实导致性能损耗可以再通过重构改善性能。
- 消除重复代码。重复代码会使目标文件体积增大。很多是通过代码拷贝实现，可能会导致错误扩散。
- 提取方法或函数。提取重复代码定义成独立的函数，函数粒度小复用的机会就大。一旦需要修改仅需修改一处 ，职责单一容易理解。若重复代码位于同一继承体系中，可以提取成基类的方法。若并不是完全重复，存在微小的差异，可以使用模板方法模式。
- 优先使用组合而非继承。通过继承和组合都可以使一个类获得另一个类的功能。但使用继承时子类与父类是强依赖关系。在使用父类指针或引用的地方都可以使用子类替代父类。也就是说只有当两个类之间确实存在`Is-A`关系时才能使用继承。强依赖关系使得子类父类耦合性很强。而组合相比较来说依赖减弱，当满足`Has-A`关系时就可以通过组合来实现。
- 降低圈复杂度。圈复杂度用来衡量一个模块判定结构的复杂程度，也可理解为覆盖所有的可能情况最少使用的测试用例数。复杂度高的代码判断逻辑复杂，可能会引入bug，且可读性很差。圈复杂度主要与分支语句（if、else、for、switch等）的个数有关。当一段代码中含有较多的分支语句，其逻辑复杂程度就会增加。有目的的降低核心类、核心方法的复杂度，可以降低软件的风险，增加软件的可扩展性。
- 合并/简化条件表达式。若一系列条件判断，得到相同结果，可以将这些测试合并为一个独立函数或方法。
- 避免多层嵌套条件表达式。条件表达式通常有两种形式：所有分支都属于正常语句以及只有一个分支属于正常语句，其他都是非正常情况。如果某个条件属于异常条件且不太常见，则应该单独检查该条件。嵌套导致代码可读性差，应尽量避免。
- 使用多态取代条件表达式。若在某个条件表达式中存在根据类型的不同具有不同的行为。可以考虑将原始函数声明为抽象函数，将条件表达式的每个分支放进一个子类重写的方法中。使用多态不必编写某些条件表达式并且若你想添加一种新类型，只需创建一个新的子类并重写该方法。这些更改对类的用户是透明的，上层不需要做任何更改。
- 免过度超前设计。过度超前的设计是指代码的灵活性和复杂性超出了所需。代码应该满足当前的需求，并留有可扩展的余地。对于未来的变化，既不要考虑的太多，也不能一点都不考虑。

## 重构示例

### 高Npath复杂度的示例

```java
private void processCaseStage(DataObject dataObject) throws DataFlowException {
	String caseStageValue = null;
	if (caseStageValue == null) {
		Object targetDate = MetaDataUtils.getMemberValue(TAjAjjbxx.D_PJSXRQ, dataObject);
		caseStageValue = targetDate == null ? null : SacwConst.AJJD_PJSX;
	}
	
	if (caseStageValue == null) {
		Object targetDate = MetaDataUtils.getMemberValue(TAjAjjbxx.D_FYLARQ, dataObject);
		caseStageValue = targetDate == null ? null : SacwConst.AJJD_FYLA;
	}
	
	if (caseStageValue == null) {
		Object targetDate = MetaDataUtils.getMemberValue(TAjAjjbxx.D_TQGSRQ, dataObject);
		caseStageValue = targetDate == null ? null : SacwConst.AJJD_TQGS;
	}
	
	if (caseStageValue == null) {
		Object targetDate = MetaDataUtils.getMemberValue(TAjAjjbxx.D_LASCKSRQ, dataObject);
		caseStageValue = targetDate == null ? null : SacwConst.AJJD_SCQSSL;
	}
	
	if (caseStageValue == null) {
		Object targetDate = MetaDataUtils.getMemberValue(TAjAjjbxx.D_YSSCQSRQ, dataObject);
		caseStageValue = targetDate == null ? null : SacwConst.AJJD_YSQS;
	}
	
	if (caseStageValue == null) {
		Object targetDate = MetaDataUtils.getMemberValue(TAjAjjbxx.D_LARQ, dataObject);
		caseStageValue = targetDate == null ? null : SacwConst.AJJD_LAZC;
	}
	
	if(caseStageValue != null){
		MetaDataUtils.putMemberValue(dataObject, TAjAjjbxx.EntityId, TAjAjjbxx.C_AJJZJD, caseStageValue);
		logger.info("---------------->处理案件所处阶段，案件所处阶段为：" + caseStageValue);
	}else{
		throw new DataParseException("立案侦查日期、移送起诉日期、审查起诉受理日期、提起公诉日期、法院立案日期、裁判生效日期 这六个日期至少需要存在一个日期");
	}
	
}
```

### 重构之后的代码

```java
private void processCaseStage(DataObject dataObject) throws DataFlowException {
	String caseStageValue = StageValueManager.newInstance().getStageValue(dataObject);
	if(caseStageValue != null){
		MetaDataUtils.putMemberValue(dataObject, TAjAjjbxx.EntityId, TAjAjjbxx.C_AJJZJD, caseStageValue);
		logger.info("---------------->处理案件所处阶段，案件所处阶段为：" + caseStageValue);
	}else{
		throw new DataParseException("立案侦查日期、移送起诉日期、审查起诉受理日期、提起公诉日期、法院立案日期、裁判生效日期 这六个日期至少需要存在一个日期");
	}
}
```

```java
/**
 * StageValueManager.
 * @author blinkfox on 2017-06-22.
 * @version 1.0
 */
public final class StageValueManager {
    
    /**
     * 初始化6种情况常量的有序map.
     */
    @SuppressWarnings("serial")
    private static final Map<String, String> ajjdMap = new LinkedHashMap<String, String>() {{
        put(TAjAjjbxx.D_PJSXRQ, SacwConst.AJJD_PJSX);
        put(TAjAjjbxx.D_FYLARQ, SacwConst.AJJD_FYLA);
        put(TAjAjjbxx.D_TQGSRQ, SacwConst.AJJD_TQGS);
        put(TAjAjjbxx.D_LASCKSRQ, SacwConst.AJJD_SCQSSL);
        put(TAjAjjbxx.D_YSSCQSRQ, SacwConst.AJJD_YSQS);
        put(TAjAjjbxx.D_LARQ, SacwConst.AJJD_LAZC);
    }};
    
    /**
     * 私有构造方法.
     */
    private StageValueManager() {
        super();
    }
    
    /**
     * 获取新实例.
     * @return StageValueManager实例
     */
    public static StageValueManager newInstance() {
        return new StageValueManager();
    }
    
    /**
     * 获取各种情况的stageValue.
     * @param dataObject dataObject
     * @return caseStageValue
     */
    public String getStageValue(DataObject dataObject) {
        String caseStageValue = null;
        for (Map.Entry<String, String> entry : ajjdMap.entrySet()) {
            if (caseStageValue == null) {
                caseStageValue = MetaDataUtils.getMemberValue(entry.getKey(), dataObject) == null ? null : entry.getValue();
            }
        }
        return caseStageValue;
    }

}
```

  [1]: http://static.blinkfox.com/refactoring.png
  [2]: http://static.blinkfox.com/refactoring-principle.jpg