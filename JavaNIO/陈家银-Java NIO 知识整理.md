# Java NIO 知识整理

---

## 一、概述

Java NIO是`java 1.4`之后新出的一套IO接口，这里的的新是相对于原有标准的Java IO和Java Networking接口。NIO提供了一种完全不同的操作方式。

> NIO中的N可以理解为Non-blocking，不单纯是New。

NIO包含下面几个核心的组件：

- Channels
- Buffers
- Selectors

整个NIO体系包含的类远远不止这几个，但是在笔者看来`Channels`, `Buffers`和`Selectors`组成了这个核心的API。其他的一些组件，比如`Pipe`和`FileLock`仅仅只作为上述三个的负责类。

### Channels and Buffers

标准的IO编程接口是面向字节流和字符流的。而**NIO是面向通道和缓冲区的，数据总是从通道中读到buffer缓冲区内，或者从buffer写入到通道中**。通常来说NIO中的所有IO都是从Channel开始的。

> **Java NIO**: Channels read data into Buffers, and Buffers write data into Channels.

有很多的Channel，Buffer类型。下面列举了主要的几种：

- FileChannel
- DatagramChannel
- SocketChannel
- ServerSocketChannel

正如你看到的，这些channel基于文件IO、UDP和TCP的网络IO。下面是核心的Buffer实现类的列表：

- ByteBuffer
- CharBuffer
- DoubleBuffer
- FloatBuffer
- intBuffer
- LongBuffer
- ShortBuffer
- MappedByteBuffer

这些Buffer涵盖了可以通过IO操作的基础类型：`byte`, `short`, `int`, `long`, `float`, `double`以及`characters`. NIO实际上还包含一种`MappedBytesBuffer`, 一般用于和内存映射的文件。

### Selectors

NIO中有一个选择器`slectors`的概念。选择器selector可以检测多个通道的事件状态（例如：链接打开，数据到达）这样单线程就可以操作多个通道的数据。如果你的程序中有大量的链接，同时每个链接的IO带宽不高的话，这个特性将会非常有帮助。比如聊天服务器。 下面是一个单线程中Slector维护3个Channel的示意图：

![Selectors](http://tutorials.jenkov.com/images/java-nio/overview-selectors.png)

> **Java NIO**: A Thread uses a Selector to handle 3 Channel's.

要使用Selector的话，我们必须把Channel注册到Selector上，然后就可以调用Selector的select()方法。这个方法会进入阻塞，直到有一个channel的状态符合条件。

### Non-blocking IO

Java NIO使我们可以进行非阻塞IO操作。比如说，单线程中从通道读取数据到buffer，同时可以继续做别的事情，当数据读取到buffer中后，线程再继续处理数据。写数据也是一样的。

## 二、Channel通道

Java NIO Channel通道和流非常相似，主要有以下几点区别：

- 通道可以读也可以写，流一般来说是单向的（只能读或者写）。
- 通道可以异步读写。
- 通道总是基于缓冲区Buffer来读写。

正如上面提到的，我们可以从通道中读取数据写入到buffer；也可以中buffer内读取数据写入到通道中。

### Channel的实现

下面列出Java NIO中最重要的集中Channel的实现：

- `FileChannel`: 用于文件的数据读写。
- `DatagramChannel`: 用于UDP的数据读写。
- `SocketChannel`: 用于TCP的数据读写。
- `ServerSocketChannel`: 允许我们监听TCP链接请求，每个请求会创建会一个`SocketChannel`。

### Channel的基础示例

这有一个利用FileChannel读取数据到Buffer的例子：

```java
RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
FileChannel inChannel = aFile.getChannel();
ByteBuffer buf = ByteBuffer.allocate(48);

int bytesRead = inChannel.read(buf);
while (bytesRead != -1) {
    System.out.println("Read " + bytesRead);
    buf.flip();

    while(buf.hasRemaining()){
        System.out.print((char) buf.get());
    }

    buf.clear();
    bytesRead = inChannel.read(buf);
}
aFile.close();
```

> **注意**: `buf.flip()`的调用。首先把数据读取到Buffer中，然后调用`flip()`方法。接着再把数据读取出来。

## 三、Buffer缓冲区

Java NIO Buffers用于和NIO Channels进行交互。正如你已经知道的，我们从channel中读取数据到buffer里，从buffer把数据写入到channel中。
**buffer本质上就是一块内存区，可以用来写入数据，并在稍后读取出来**。这块内存被NIO Buffer包裹起来，对外提供一系列的读写方便开发的接口。

### Buffer基本用法

利用Buffer读写数据，通常遵循四个步骤：

- 把数据写入buffer；
- 调用`flip()`方法；
- 从Buffer中读取数据；
- 调用`buffer.clear()`或者`buffer.compact()`。

当写入数据到buffer中时，buffer会记录已经写入的数据大小。当需要读数据时，通过`flip()`方法把buffer从写模式调整为读模式；在读模式下，可以读取所有已经写入的数据。

当读取完数据后，需要清空buffer，以满足后续写入操作。清空buffer有两种方式：调用`clear()`或`compact()`方法。`clear()`会清空整个buffer，`compact()`则只清空已读取的数据，未被读取的数据会被移动到buffer的开始位置，写入位置则近跟着未读数据之后。

### Buffer的容量，位置，上限（Capacity, Position and Limit）

buffer缓冲区实质上就是一块内存，用于写入数据，也供后续再次读取数据。这块内存被NIO Buffer管理，并提供一系列的方法用于更简单的操作这块内存。
一个Buffer有三个属性是必须掌握的，分别是：

- `capacity`: 容量
- `position`: 位置
- `limit`: 限制

position和limit的具体含义取决于当前buffer的模式。capacity在两种模式下都表示容量。

下面有张示例图，描诉了不同模式下position和limit的含义：

![position和limit的含义](http://tutorials.jenkov.com/images/java-nio/buffers-modes.png)

> **Buffer capacity, position and limit in write and read mode.**

### 容量（Capacity）

作为一块内存，buffer有一个固定的大小，叫做`capacity`容量。也就是最多只能写入容量值得字节，整形等数据。一旦buffer写满了就需要清空已读数据以便下次继续写入新的数据。

### 位置（Position）

当写入数据到Buffer的时候需要从一个确定的位置开始，默认初始化时这个位置position为`0`，一旦写入了数据比如一个字节，整形数据，那么position的值就会指向数据之后的一个单元，position最大可以到`capacity-1`。

当从Buffer读取数据时，也需要从一个确定的位置开始。buffer从写入模式变为读取模式时，position会归零，每次读取后，position向后移动。

### 上限（Limit）

在写模式，limit的含义是我们所能写入的最大数据量。它等同于buffer的容量。

一旦切换到读模式，limit则代表我们所能读取的最大数据量，他的值等同于写模式下position的位置。

数据读取的上限时buffer中已有的数据，也就是limit的位置（原position所指的位置）。

### 分配一个Buffer（Allocating a Buffer）

为了获取一个Buffer对象，你必须先分配。每个Buffer实现类都有一个`allocate()`方法用于分配内存。下面看一个实例,开辟一个`48`字节大小的buffer：

```java
ByteBuffer buf = ByteBuffer.allocate(48);
```

开辟一个`1024`个字符的CharBuffer：

```java
CharBuffer buf = CharBuffer.allocate(1024);
```

### 写入数据到Buffer（Writing Data to a Buffer）

写数据到Buffer有两种方法：

- 从Channel中写数据到Buffer
- 手动写数据到Buffer，调用`put`方法

下面是一个实例，演示从Channel写数据到Buffer：

```java
int bytesRead = inChannel.read(buf); //read into buffer.
```

通过`put()`写数据：

```java
buf.put(127);
```

`put()`方法有很多不同版本，对应不同的写数据方法。例如把数据写到特定的位置，或者把一个字节数据写入buffer。看考JavaDoc文档可以查阅的更多数据。

### 翻转（flip()）

`flip()`方法可以吧Buffer从写模式切换到读模式。调用`flip()`方法会把position归零，并设置limit为之前的position的值。 也就是说，现在position代表的是读取位置，limit标示的是已写入的数据位置。

### 从Buffer读取数据（Reading Data from a Buffer）

冲Buffer读数据也有两种方式：

- 从buffer读数据到channel
- 从buffer直接读取数据，调用`get()`方法

读取数据到channel的例子：

```java
//read from buffer into channel.
int bytesWritten = inChannel.write(buf);
```

调用`get()`读取数据的例子：

```java
byte aByte = buf.get();
```

`get()`方法也有诸多版本，对应了不同的读取方式。

### rewind()

`Buffer.rewind()`方法将position置为`0`，这样我们可以重复读取buffer中的数据。limit保持不变。

### clear() and compact()

一旦我们从buffer中读取完数据，需要复用buffer为下次写数据做准备。只需要调用`clear()`或`compact()`方法。

`clear()`方法会重置position为0，limit为capacity，也就是整个Buffer清空。实际上Buffer中数据并没有清空，我们只是把标记为修改了。

如果Buffer还有一些数据没有读取完，调用`clear()`就会导致这部分数据被“遗忘”，因为我们没有标记这部分数据未读。

针对这种情况，如果需要保留未读数据，那么可以使用`compact()`。 因此`compact()`和`clear()`的区别就在于对未读数据的处理，是保留这部分数据还是一起清空。

### mark() and reset()

通过`mark()`方法可以标记当前的position，通过`reset()`方法来恢复mark的位置，这个非常像canva的save和restore：

```java
buffer.mark();
//call buffer.get() a couple of times, e.g. during parsing.
buffer.reset();  //set position back to mark.
```

### equals() and compareTo()

可以用`eqauls()`和`compareTo()`比较两个buffer。

#### equals()

使用`equals()`判断两个buffer相对，需满足：

- 类型相同
- buffer中剩余字节数相同
- 所有剩余字节相等

从上面的三个条件可以看出，`equals()`只比较buffer中的部分内容，并不会去比较每一个元素。

#### compareTo()

`compareTo()`也是比较buffer中的剩余元素，只不过这个方法适用于比较排序的。

## 四、Scatter / Gather

Java NIO发布时内置了对`scatter / gather`的支持。`scatter / gather`是通过通道读写数据的两个概念。

`Scattering read`指的是从通道读取的操作能把数据写入多个buffer，也就是sctters代表了数据从一个channel到多个buffer的过程。

`gathering write`则正好相反，表示的是从多个buffer把数据写入到一个channel中。

`Scatter / gather`在有些场景下会非常有用，比如需要处理多份分开传输的数据。举例来说，假设一个消息包含了header和body，我们可能会把header和body保存在不同独立buffer中，这种分开处理header与body的做法会使开发更简明。

### Scattering Read

"scattering read"是把数据从单个Channel写入到多个buffer，下面是示意图：

![Scattering Read](http://tutorials.jenkov.com/images/java-nio/scatter.png)

> **Java NIO: Scattering Read**

用代码来表示的话如下：

```java
ByteBuffer header = ByteBuffer.allocate(128);
ByteBuffer body   = ByteBuffer.allocate(1024);

ByteBuffer[] bufferArray = { header, body };

channel.read(bufferArray);
```

观察代码可以发现，我们把多个buffer写在了一个数组中，然后把数组传递给`channel.read()`方法。`read()`方法内部会负责把数据按顺序写进传入的buffer数组内。一个buffer写满后，接着写到下一个buffer中。

实际上，`scattering read`内部必须写满一个buffer后才会向后移动到下一个buffer，因此这并不适合消息大小会动态改变的部分，也就是说，如果你有一个header和body，并且header有一个固定的大小（比如128字节）,这种情形下可以正常工作。

### Gathering Write

`gathering write`把多个buffer的数据写入到同一个channel中，下面是示意图：

![Gathering Write](http://tutorials.jenkov.com/images/java-nio/gather.png)

> **Java NIO: Gathering Write**

用代码表示的话如下：

```java
ByteBuffer header = ByteBuffer.allocate(128);
ByteBuffer body   = ByteBuffer.allocate(1024);

//write data into buffers

ByteBuffer[] bufferArray = { header, body };

channel.write(bufferArray);
```

类似的传入一个buffer数组给write，内部机会按顺序将数组内的内容写进channel，这里需要注意，写入的时候针对的是buffer中position到limit之间的数据。也就是如果buffer的容量是128字节，但它只包含了58字节数据，那么写入的时候只有58字节会真正写入。因此`gathering write`是可以适用于可变大小的message的，这和`scattering read`不同。

## 五、Channel to Channel Transfers通道传输接口

在Java NIO中如果一个channel是`FileChannel`类型的，那么他可以直接把数据传输到另一个channel。逐个特性得益于`FileChannel`包含的`transferTo`和`transferFrom`两个方法。

### transferFrom()

`FileChannel.transferFrom`方法把数据从通道源传输到FileChannel：

```java
RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
FileChannel fromChannel = fromFile.getChannel();

RandomAccessFile toFile = new RandomAccessFile("toFile.txt", "rw");
FileChannel toChannel = toFile.getChannel();

long position = 0;
long count = fromChannel.size();

toChannel.transferFrom(fromChannel, position, count);
```

transferFrom的参数`position`和`count`表示目标文件的写入位置和最多写入的数据量。如果通道源的数据小于count那么就传实际有的数据量。 另外，有些`SocketChannel`的实现在传输时只会传输哪些处于就绪状态的数据，即使`SocketChannel`后续会有更多可用数据。因此，这个传输过程可能不会传输整个的数据。

### transferTo()

`transferTo`方法把FileChannel数据传输到另一个channel,下面是案例：

```java
RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
FileChannel fromChannel = fromFile.getChannel();

RandomAccessFile toFile = new RandomAccessFile("toFile.txt", "rw");
FileChannel toChannel = toFile.getChannel();

long position = 0;
long count = fromChannel.size();

fromChannel.transferTo(position, count, toChannel);
```

这段代码和之前介绍transfer时的代码非常相似，区别只在于调用方法的是哪个FileChannel。

## 六、Selector

Selector（选择器）是Java NIO中能够检测一到多个NIO通道，并能够知晓通道是否为诸如读写事件做好准备的组件。这样，一个单独的线程可以管理多个channel，从而管理多个网络连接。

### 为什么使用Selector

仅用单个线程来处理多个Channels的好处是，只需要更少的线程来处理通道。事实上，**可以只用一个线程处理所有的通道**。对于操作系统来说，线程之间上下文切换的开销很大，而且每个线程都要占用系统的一些资源（如内存）。因此，使用的线程越少越好。

但是，需要记住，现代的操作系统和CPU在多任务方面表现的越来越好，所以多线程的开销随着时间的推移，变得越来越小了。实际上，如果一个CPU有多个内核，不使用多任务可能是在浪费CPU能力。不管怎么说，关于那种设计的讨论应该放在另一篇不同的文章中。在这里，只要知道使用**Selector能够处理多个通道**就足够了。

下面是单线程使用一个Selector处理3个channel的示例图：

![Selector](http://ifeve.com/wp-content/uploads/2013/06/overview-selectors.png)

### Selector的创建

通过调用`Selector.open()`方法创建一个Selector，如下：

```java
Selector selector = Selector.open();
```

### 向Selector注册通道

为了将Channel和Selector配合使用，必须将channel注册到selector上。通过`SelectableChannel.register()`方法来实现，如下：

```java
channel.configureBlocking(false);
SelectionKey key = channel.register(selector, Selectionkey.OP_READ);
```

与Selector一起使用时，Channel必须处于非阻塞模式下。这意味着**不能将FileChannel与Selector一起使用**，因为FileChannel不能切换到非阻塞模式。而套接字通道都可以。

注意`register()`方法的第二个参数。这是一个“interest集合”，意思是在通过Selector监听Channel时对什么事件感兴趣。可以监听四种不同类型的事件：

- `Connect`: 连接就绪
- `Accept`: 接收就绪
- `Read`: 读就绪
- `Write`: 写就绪

通道触发了一个事件意思是该事件已经就绪。所以，某个channel成功连接到另一个服务器称为“连接就绪”。一个server socket channel准备好接收新进入的连接称为“接收就绪”。一个有数据可读的通道可以说是“读就绪”。等待写数据的通道可以说是“写就绪”。

这四种事件用SelectionKey的四个常量来表示：

- SelectionKey.OP_CONNECT
- SelectionKey.OP_ACCEPT
- SelectionKey.OP_READ
- SelectionKey.OP_WRITE

如果你对不止一种事件感兴趣，那么可以用“位或”操作符将常量连接起来，如下：

```java
int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
```

### SelectionKey

在上一小节中，当向Selector注册Channel时，`register()`方法会返回一个SelectionKey对象。这个对象包含了一些你感兴趣的属性：

- interest集合
- ready集合
- Channel
- Selector
- 附加的对象（可选）

下面我会描述这些属性。

#### interest集合

就像向Selector注册通道一节中所描述的，interest集合是你所选择的感兴趣的事件集合。可以通过SelectionKey读写interest集合，像这样：

```java
int interestSet = selectionKey.interestOps();

boolean isInterestedInAccept  = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT；
boolean isInterestedInConnect = interestSet & SelectionKey.OP_CONNECT;
boolean isInterestedInRead    = interestSet & SelectionKey.OP_READ;
boolean isInterestedInWrite   = interestSet & SelectionKey.OP_WRITE;
```

可以看到，用“位与”操作interest 集合和给定的SelectionKey常量，可以确定某个确定的事件是否在interest 集合中。

#### ready集合

ready 集合是通道已经准备就绪的操作的集合。在一次选择(Selection)之后，你会首先访问这个ready set。Selection将在下一小节进行解释。可以这样访问ready集合：

```java
int readySet = selectionKey.readyOps();
```

可以用像检测interest集合那样的方法，来检测channel中什么事件或操作已经就绪。但是，也可以使用以下四个方法，它们都会返回一个布尔类型：

- selectionKey.isAcceptable();
- selectionKey.isConnectable();
- selectionKey.isReadable();
- selectionKey.isWritable();

#### Channel + Selector

从SelectionKey访问Channel和Selector很简单。如下：

```java
Channel  channel  = selectionKey.channel();
Selector selector = selectionKey.selector();
```

#### 附加的对象

可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道。例如，可以附加与通道一起使用的Buffer，或是包含聚集数据的某个对象。使用方法如下：

```java
selectionKey.attach(theObject);
Object attachedObj = selectionKey.attachment();
```

还可以在用`register()`方法向Selector注册Channel的时候附加对象。如：

```java
SelectionKey key = channel.register(selector, SelectionKey.OP_READ, theObject);
```

### 通过Selector选择通道

一旦向Selector注册了一或多个通道，就可以调用几个重载的select()方法。这些方法返回你所感兴趣的事件（如连接、接受、读或写）已经准备就绪的那些通道。换句话说，如果你对“读就绪”的通道感兴趣，select()方法会返回读事件已经就绪的那些通道。

下面是select()方法：

- `int select()`：阻塞到至少有一个通道在你注册的事件上就绪了。
- `int select(long timeout)`：和select()一样，除了最长会阻塞timeout毫秒(参数)。
- `int selectNow()`：不会阻塞，不管什么通道就绪都立刻返回（译者注：此方法执行非阻塞的选择操作。如果自从前一次选择操作后，没有通道变成可选择的，则此方法直接返回零。）。

select()方法返回的int值表示有多少通道已经就绪。亦即，自上次调用select()方法后有多少通道变成就绪状态。如果调用select()方法，因为有一个通道变成就绪状态，返回了1，若再次调用select()方法，如果另一个通道就绪了，它会再次返回1。如果对第一个就绪的channel没有做任何操作，现在就有两个就绪的通道，但在每次select()方法调用之间，只有一个通道就绪了。

#### selectedKeys()

一旦调用了select()方法，并且返回值表明有一个或更多个通道就绪了，然后可以通过调用selector的selectedKeys()方法，访问“已选择键集（selected key set）”中的就绪通道。

当像Selector注册Channel时，`Channel.register()`方法会返回一个SelectionKey对象。这个对象代表了注册到该Selector的通道。可以通过SelectionKey的selectedKeySet()方法访问这些对象。

可以遍历这个已选择的键集合来访问就绪的通道。如下：

```java
Set selectedKeys = selector.selectedKeys();
Iterator keyIterator = selectedKeys.iterator();
while(keyIterator.hasNext()) {
    SelectionKey key = keyIterator.next();
    if(key.isAcceptable()) {
        // a connection was accepted by a ServerSocketChannel.
    } else if (key.isConnectable()) {
        // a connection was established with a remote server.
    } else if (key.isReadable()) {
        // a channel is ready for reading
    } else if (key.isWritable()) {
        // a channel is ready for writing
    }
    keyIterator.remove();
}
```

这个循环遍历已选择键集中的每个键，并检测各个键所对应的通道的就绪事件。

注意每次迭代末尾的`keyIterator.remove()`调用。Selector不会自己从已选择键集中移除SelectionKey实例。必须在处理完通道时自己移除。下次该通道变成就绪时，Selector会再次将其放入已选择键集中。

`SelectionKey.channel()`方法返回的通道需要转型成你要处理的类型，如ServerSocketChannel或SocketChannel等。

#### wakeUp()

某个线程调用select()方法后阻塞了，即使没有通道已经就绪，也有办法让其从select()方法返回。只要让其它线程在第一个线程调用select()方法的那个对象上调用`Selector.wakeup()`方法即可。阻塞在select()方法上的线程会立马返回。

如果有其它线程调用了wakeup()方法，但当前没有线程阻塞在select()方法上，下个调用select()方法的线程会立即“醒来（wake up）”。

#### close()

用完Selector后调用其close()方法会关闭该Selector，且使注册到该Selector上的所有SelectionKey实例无效。通道本身并不会关闭。

### 完整的示例

这里有一个完整的示例，打开一个Selector，注册一个通道注册到这个Selector上(通道的初始化过程略去),然后持续监控这个Selector的四种事件（接受，连接，读，写）是否就绪。

```java
Selector selector = Selector.open();
channel.configureBlocking(false);
SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
while(true) {
    int readyChannels = selector.select();
    if(readyChannels == 0) continue;
    Set selectedKeys = selector.selectedKeys();
    Iterator keyIterator = selectedKeys.iterator();
    while(keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        if(key.isAcceptable()) {
            // a connection was accepted by a ServerSocketChannel.
        } else if (key.isConnectable()) {
            // a connection was established with a remote server.
        } else if (key.isReadable()) {
            // a channel is ready for reading
        } else if (key.isWritable()) {
            // a channel is ready for writing
        }
        keyIterator.remove();
    }
}
```

## 七、FileChannel

Java NIO中的FileChannel是一个连接到文件的通道。可以通过文件通道读写文件。

FileChannel无法设置为非阻塞模式，它总是运行在阻塞模式下。

### 打开FileChannel

在使用FileChannel之前，必须先打开它。但是，我们**无法直接打开一个FileChannel，需要通过使用一个InputStream、OutputStream或RandomAccessFile来获取一个FileChannel实例**。下面是通过RandomAccessFile打开FileChannel的示例：

```java
RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
FileChannel inChannel = aFile.getChannel();
```

### 从FileChannel读取数据

调用多个read()方法之一从FileChannel中读取数据。如：

```java
ByteBuffer buf = ByteBuffer.allocate(48);
int bytesRead = inChannel.read(buf);
```

首先，分配一个Buffer。从FileChannel中读取的数据将被读到Buffer中。

然后，调用`FileChannel.read()`方法。该方法将数据从FileChannel读取到Buffer中。**read()方法返回的int值表示了有多少字节被读到了Buffer中。如果返回-1，表示到了文件末尾。**

### 向FileChannel写数据

使用`FileChannel.write()`方法向FileChannel写数据，该方法的参数是一个Buffer。如：

```java
String newData = "New String to write to file..." + System.currentTimeMillis();

ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();
buf.put(newData.getBytes());
buf.flip();

while(buf.hasRemaining()) {
    channel.write(buf);
}
```

> **注意**: `FileChannel.write()`是在while循环中调用的。因为无法保证write()方法一次能向FileChannel写入多少字节，因此需要重复调用write()方法，直到Buffer中已经没有尚未写入通道的字节。

### 关闭FileChannel

用完FileChannel后必须将其关闭。如：

```java
channel.close();
```

### FileChannel的position方法

有时可能需要在FileChannel的某个特定位置进行数据的读/写操作。可以通过调用position()方法获取FileChannel的当前位置。

也可以通过调用`position(long pos)`方法设置FileChannel的当前位置。

这里有两个例子:

```java
long pos = channel.position();
channel.position(pos + 123);
```

如果将位置设置在文件结束符之后，然后试图从文件通道中读取数据，读方法将返回-1(文件结束标志)。

如果将位置设置在文件结束符之后，然后向通道中写数据，文件将撑大到当前位置并写入数据。这可能导致“文件空洞”，磁盘上物理文件中写入的数据间有空隙。

### FileChannel的size方法

FileChannel实例的`size()`方法将返回该实例所关联文件的大小。如:

```java
long fileSize = channel.size();
```

### FileChannel的truncate方法

可以使用`FileChannel.truncate()`方法截取一个文件。截取文件时，文件将中指定长度后面的部分将被删除。如：

```java
channel.truncate(1024);
```

这个例子截取文件的前1024个字节。

### FileChannel的force方法

`FileChannel.force()`方法将通道里尚未写入磁盘的数据强制写到磁盘上。出于性能方面的考虑，操作系统会将数据缓存在内存中，所以无法保证写入到FileChannel里的数据一定会即时写到磁盘上。要保证这一点，需要调用`force()`方法。

force()方法有一个boolean类型的参数，指明是否同时将文件元数据（权限信息等）写到磁盘上。

下面的例子同时将文件数据和元数据强制写到磁盘上：

```java
channel.force(true);
```

## 八、SocketChannel

Java NIO中的SocketChannel是一个连接到TCP网络套接字的通道。

### 打开 SocketChannel

下面是SocketChannel的打开方式：

```java
SocketChannel socketChannel = SocketChannel.open();
socketChannel.connect(new InetSocketAddress("http://jenkov.com", 80));
```

打开一个SocketChannel并连接到互联网上的某台服务器。一个新连接到达ServerSocketChannel时，会创建一个SocketChannel。

### 关闭 SocketChannel

当用完SocketChannel之后调用`SocketChannel.close()`关闭SocketChannel：

```java
socketChannel.close();
```

### 从 SocketChannel 读取数据

要从SocketChannel中读取数据，调用一个`read()`的方法之一。以下是例子：

```java
ByteBuffer buf = ByteBuffer.allocate(48);
int bytesRead = socketChannel.read(buf);
```

首先，分配一个Buffer。从SocketChannel读取到的数据将会放到这个Buffer中。

然后，调用`SocketChannel.read()`。该方法将数据从SocketChannel读到Buffer中。read()方法返回的int值表示读了多少字节进Buffer里。如果返回的是-1，表示已经读到了流的末尾（连接关闭了）。

### 写入 SocketChannel

写数据到SocketChannel用的是`SocketChannel.write()`方法，该方法以一个Buffer作为参数。示例如下：

```java
String newData = "New String to write to file..." + System.currentTimeMillis();

ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();
buf.put(newData.getBytes());

buf.flip();

while(buf.hasRemaining()) {
    channel.write(buf);
}
```

注意`SocketChannel.write()`方法的调用是在一个while循环中的。`write()`方法无法保证能写多少字节到SocketChannel。所以，我们重复调用`write()`直到Buffer没有要写的字节为止。

### SocketChannel的非阻塞模式

可以设置SocketChannel为非阻塞模式（non-blocking mode）。设置之后，就可以在异步模式下调用`connect()`，`read()`和`write()`了。

#### connect()

如果SocketChannel在非阻塞模式下，此时调用`connect()`，该方法可能在连接建立之前就返回了。为了确定连接是否建立，可以调用`finishConnect()`的方法。像这样：

```java
socketChannel.configureBlocking(false);
socketChannel.connect(new InetSocketAddress("http://jenkov.com", 80));

while(!socketChannel.finishConnect() ){
    //wait, or do something else...
}
```

#### write()

非阻塞模式下，`write()`方法在尚未写出任何内容时可能就返回了。所以需要在循环中调用`write()`。前面已经有例子了，这里就不赘述了。

#### read()

非阻塞模式下,`read()`方法在尚未读取到任何数据时可能就返回了。所以需要关注它的int返回值，它会告诉你读取了多少字节。

### 非阻塞模式与选择器

非阻塞模式与选择器搭配会工作的更好，通过将一或多个SocketChannel注册到Selector，可以询问选择器哪个通道已经准备好了读取，写入等。Selector与SocketChannel的搭配使用会在后面详讲。

## 九、ServerSocketChannel

Java NIO中的`ServerSocketChannel`是一个可以监听新进来的TCP连接的通道, 就像标准IO中的ServerSocket一样。ServerSocketChannel类在 `java.nio.channels`包中。

这里有个例子：

```java
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

serverSocketChannel.socket().bind(new InetSocketAddress(9999));

while (true) {
    SocketChannel socketChannel = serverSocketChannel.accept();

    //do something with socketChannel...
}
```

### 打开 ServerSocketChannel

通过调用`ServerSocketChannel.open()`方法来打开ServerSocketChannel。如：

```java
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
```

### 关闭 ServerSocketChannel

通过调用`ServerSocketChannel.close()`方法来关闭ServerSocketChannel. 如：

```java
serverSocketChannel.close();
```

### 监听新进来的连接

通过`ServerSocketChannel.accept()`方法监听新进来的连接。当`accept()`方法返回的时候,它返回一个包含新进来的连接的 SocketChannel。因此, `accept()`方法会一直阻塞到有新连接到达。

通常不会仅仅只监听一个连接，在while循环中调用`accept()`方法. 如下面的例子：

```java
while (true) {
    SocketChannel socketChannel = serverSocketChannel.accept();

    //do something with socketChannel...
}
```

当然,也可以在while循环中使用除了true以外的其它退出准则。

### ServerSocketChannel的非阻塞模式

ServerSocketChannel可以设置成非阻塞模式。在非阻塞模式下，`accept()`方法会立刻返回，如果还没有新进来的连接,返回的将是null。 因此，需要检查返回的SocketChannel是否是null。如：

```java
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

serverSocketChannel.socket().bind(new InetSocketAddress(9999));
serverSocketChannel.configureBlocking(false);

while (true) {
    SocketChannel socketChannel = serverSocketChannel.accept();

    if (socketChannel != null) {
        //do something with socketChannel...
    }
}
```

## 十、DatagramChannel

Java NIO中的`DatagramChannel`是一个能收发UDP包的通道。因为UDP是无连接的网络协议，所以不能像其它通道那样读取和写入。它发送和接收的是数据包。

### 打开 DatagramChannel

下面是 DatagramChannel 的打开方式：

```java
DatagramChannel channel = DatagramChannel.open();
channel.socket().bind(new InetSocketAddress(9999));
```

这个例子打开的DatagramChannel可以在UDP端口9999上接收数据包。

### 接收数据

通过`receive()`方法从DatagramChannel接收数据，如：

```java
ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();
channel.receive(buf);
```

`receive()`方法会将接收到的数据包内容复制到指定的Buffer. 如果Buffer容不下收到的数据，多出的数据将被丢弃。

### 发送数据

通过`send()`方法从DatagramChannel发送数据，如:

```java
String newData = "New String to write to file..." + System.currentTimeMillis();

ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();
buf.put(newData.getBytes());
buf.flip();

int bytesSent = channel.send(buf, new InetSocketAddress("jenkov.com", 80));
```

这个例子发送一串字符到`jenkov.com`服务器的UDP端口80。 因为服务端并没有监控这个端口，所以什么也不会发生。也不会通知你发出的数据包是否已收到，因为UDP在数据传送方面没有任何保证。

### 连接到特定的地址

可以将DatagramChannel“连接”到网络中的特定地址的。由于UDP是无连接的，连接到特定地址并不会像TCP通道那样创建一个真正的连接。而是锁住DatagramChannel ，让其只能从特定地址收发数据。

这里有个例子:

```java
channel.connect(new InetSocketAddress("jenkov.com", 80));
```

当连接后，也可以使用`read()`和`write()`方法，就像在用传统的通道一样。只是在数据传送方面没有任何保证。这里有几个例子：

```java
int bytesRead = channel.read(buf);
int bytesWritten = channel.write(buf);
```

## 十一、Pipe

Java NIO 管道是2个线程之间的单向数据连接。Pipe有一个`source`通道和一个`sink`通道。数据会被写到sink通道，从source通道读取。

这里是Pipe原理的图示：

![Pipe](http://ifeve.com/wp-content/uploads/2013/06/pipe.bmp)

### 创建管道

通过`Pipe.open()`方法打开管道。例如：

```java
Pipe pipe = Pipe.open();
```

### 向管道写数据

要向管道写数据，需要访问sink通道。像这样：

```java
Pipe.SinkChannel sinkChannel = pipe.sink();
```

通过调用SinkChannel的`write()`方法，将数据写入SinkChannel,像这样：

```java
String newData = "New String to write to file..." + System.currentTimeMillis();
ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();
buf.put(newData.getBytes());

buf.flip();

while(buf.hasRemaining()) {
    sinkChannel.write(buf);
}
```

### 从管道读取数据

从读取管道的数据，需要访问source通道，像这样：

```java
Pipe.SourceChannel sourceChannel = pipe.source();
```

调用source通道的`read()`方法来读取数据，像这样：

```java
ByteBuffer buf = ByteBuffer.allocate(48);
int bytesRead = sourceChannel.read(buf);
```

`read()`方法返回的int值会告诉我们多少字节被读进了缓冲区。

## 十二、Java NIO与IO的对比

### 面向流与面向缓冲

Java NIO和IO之间第一个最大的区别是，**IO是面向流的，NIO是面向缓冲区的**。

Java IO面向流意味着每次从流中读一个或多个字节，直至读取所有字节，它们没有被缓存在任何地方。此外，它不能前后移动流中的数据。如果需要前后移动从流中读取的数据，需要先将它缓存到一个缓冲区。

Java NIO的缓冲导向方法略有不同。数据读取到一个它稍后处理的缓冲区，需要时可在缓冲区中前后移动。这就增加了处理过程中的灵活性。但是，还需要检查是否该缓冲区中包含所有您需要处理的数据。而且，需确保当更多的数据读入缓冲区时，不要覆盖缓冲区里尚未处理的数据。

### 阻塞与非阻塞IO

Java IO的各种流是阻塞的。这意味着，当一个线程调用`read()`或`write()`时，该线程被阻塞，直到有一些数据被读取，或数据完全写入。该线程在此期间不能再干任何事情了。

Java NIO的非阻塞模式，使一个线程从某通道发送请求读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取。而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可以继续做其他的事情。 非阻塞写也是如此。一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。 线程通常将非阻塞IO的空闲时间用于在其它通道上执行IO操作，所以一个单独的线程现在可以管理多个输入和输出通道（channel）。

### 选择器（Selectors）

Java NIO的选择器允许一个单独的线程来监视多个输入通道，你可以注册多个通道使用一个选择器，然后使用一个单独的线程来“选择”通道：这些通道里已经有可以处理的输入，或者选择已准备写入的通道。这种选择机制，使得一个单独的线程很容易来管理多个通道。

### NIO和IO如何影响应用程序的设计

无论您选择IO或NIO工具箱，可能会影响您应用程序设计的以下几个方面：

- 对NIO或IO类的API调用。
- 数据处理。
- 用来处理数据的线程数。

#### API调用

当然，使用NIO的API调用时看起来与使用IO时有所不同，但这并不意外，因为并不是仅从一个InputStream逐字节读取，而是数据必须先读入缓冲区再处理。

#### 数据处理

使用纯粹的NIO设计相较IO设计，数据处理也受到影响。

在IO设计中，我们从InputStream或Reader逐字节读取数据。假设你正在处理一基于行的文本数据流，例如：

```bash
Name: Anna
Age: 25
Email: anna@mailserver.com
Phone: 1234567890
```

该文本行的流可以这样处理：

```java
InputStream input = … ; // get the InputStream from the client socket
BufferedReader reader = new BufferedReader(new InputStreamReader(input));

String nameLine   = reader.readLine();
String ageLine    = reader.readLine();
String emailLine  = reader.readLine();
String phoneLine  = reader.readLine();
```

请注意处理状态由程序执行多久决定。换句话说，一旦`reader.readLine()`方法返回，你就知道肯定文本行就已读完，`readline()`阻塞直到整行读完，这就是原因。你也知道此行包含名称；同样，第二个`readline()`调用返回的时候，你知道这行包含年龄等。 正如你可以看到，该处理程序仅在有新数据读入时运行，并知道每步的数据是什么。一旦正在运行的线程已处理过读入的某些数据，该线程不会再回退数据（大多如此）。下图也说明了这条原则：

![从一个阻塞的流中读数据](http://dl2.iteye.com/upload/attachment/0096/5635/d816b6e7-0b89-3cbf-bc24-dc7e1bb971de.png)

而一个NIO的实现会有所不同，下面是一个简单的例子：

```java
ByteBuffer buffer = ByteBuffer.allocate(48);
int bytesRead = inChannel.read(buffer);
```

注意第二行，从通道读取字节到ByteBuffer。当这个方法调用返回时，你不知道你所需的所有数据是否在缓冲区内。你所知道的是，该缓冲区包含一些字节，这使得处理有点困难。
假设第一次`read(buffer)`调用后，读入缓冲区的数据只有半行，例如，“Name:An”，你能处理数据吗？显然不能，需要等待，直到整行数据读入缓存，在此之前，对数据的任何处理毫无意义。

所以，你怎么知道是否该缓冲区包含足够的数据可以处理呢？好了，你不知道。发现的方法只能查看缓冲区中的数据。其结果是，在你知道所有数据都在缓冲区里之前，你必须检查几次缓冲区的数据。这不仅效率低下，而且可以使程序设计方案杂乱不堪。例如：

```java
ByteBuffer buffer = ByteBuffer.allocate(48);
int bytesRead = inChannel.read(buffer);
while (!bufferFull(bytesRead)) {
    bytesRead = inChannel.read(buffer);
}
```

`bufferFull()`方法必须跟踪有多少数据读入缓冲区，并返回真或假，这取决于缓冲区是否已满。换句话说，如果缓冲区准备好被处理，那么表示缓冲区满了。

`bufferFull()`方法扫描缓冲区，但必须保持在`bufferFull()`方法被调用之前状态相同。如果没有，下一个读入缓冲区的数据可能无法读到正确的位置。这是不可能的，但却是需要注意的又一问题。

如果缓冲区已满，它可以被处理。如果它不满，并且在你的实际案例中有意义，你或许能处理其中的部分数据。但是许多情况下并非如此。下图展示了“缓冲区数据循环就绪”：

![从一个通道里读数据，直到所有的数据都读到缓冲区里](http://dl2.iteye.com/upload/attachment/0096/5637/e97ec9e9-62d4-3375-80a6-d4238d6a0664.png)

### 总结

NIO可让您只使用一个（或几个）单线程管理多个通道（网络连接或文件），但付出的代价是解析数据可能会比从一个阻塞流中读取数据更复杂。

如果需要管理同时打开的成千上万个连接，这些连接每次只是发送少量的数据，例如聊天服务器，实现NIO的服务器可能是一个优势。同样，如果你需要维持许多打开的连接到其他计算机上，如P2P网络中，使用一个单独的线程来管理你所有出站连接，可能是一个优势。一个线程多个连接的设计方案如下图所示：

![单线程管理多个连接](http://dl2.iteye.com/upload/attachment/0096/5639/8c8b13c9-0d38-3599-99d3-e0d1aa90589d.png)

如果你有少量的连接使用非常高的带宽，一次发送大量的数据，也许典型的IO服务器实现可能非常契合。下图说明了一个典型的IO服务器设计：

![一个典型的IO服务器设计：一个连接通过一个线程处理](http://dl2.iteye.com/upload/attachment/0096/5641/72c44e71-8219-3989-a787-b67ced3c7ab1.png)

---

参考文档：

- [Java NIO 简明教程](https://java-nio.avenwu.net/)