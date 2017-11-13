#### 什么是同步？什么是异步？

同步和异步是针对应用程序与内核的交互而言。 

同步：指的是用户进程触发IO操作并等待或者轮询的去查看IO操作是否就绪。自己上街买衣服，自己亲自干这件事，别的事干不了。 

异步：异步是指用户进程触发IO操作以后便开始做自己的事情，而当IO操作已经完成的时候会得到IO完成的通知（异步的特点就是通知） 告诉朋友自己合适衣服的尺寸，大小，颜色，让朋友委托去卖，然后自己可以去干别的事。（使用异步IO时，Java将IO读写委托给OS处理，需要将数据缓冲区地址和大小传给OS） 

#### 什么是阻塞？什么是非阻塞？

阻塞：所谓阻塞方式的意思是指, 当试图对该文件描述符进行读写时,如果当时没有东西可读,或者暂时不可写,程序就进入等待 状态, 直到有东西可读或者可写为止去公交站充值，发现这个时候，充值员不在（可能上厕所去了），然后我们就在这里等待，一直等到充值员回来为止。 

非阻塞：非阻塞状态下, 如果没有东西可读, 或者不可写, 读写函数马上返回, 而不会等待， 银行里取款办业务时，领取一张小票，领取完后我们自己可以玩玩手机，或者与别人聊聊天，当轮我们时，银行的喇叭会通知，这时候我们就可以去了。

阻塞和非阻塞是针对于进程在访问数据的时候，根据IO操作的就绪状态来采取的不同方式，说白了是一种读取或者写入操作函数的实现方式，阻塞方式下读取或者写入函数将一直等待，而非阻塞方式下，读取或者写入函数会立即返回一个状态值。

#### IO操作可以分为3类：同步阻塞（即早期的BIO操作）、同步非阻塞（NIO）、异步非阻塞（AIO）。 

同步阻塞(BIO)：在此种方式下，用户进程在发起一个IO操作以后，必须等待IO操作的完成，只有当真正完成了IO操作以后，用户进程才能运行。JAVA传统的IO模型属于此种方式。 

同步非阻塞(NIO)：在此种方式下，用户进程发起一个IO操作以后便可返回做其它事情，但是用户进程需要时不时的询问IO操作是否就绪，这就要求用户进程不停的去询问，从而引入不必要的CPU资源浪费。其中目前JAVA的NIO就属于同步非阻塞IO。 

异步非阻塞(AIO)：此种方式下是指应用发起一个IO操作以后，不等待内核IO操作的完成，等内核完成IO操作以后会通知应用程序。

同步阻塞IO（JAVA BIO）： 
   同步并阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，当然可以通过线程池机制改善。 

同步非阻塞IO(Java NIO)： 
   同步非阻塞，服务器实现模式为一个请求一个线程，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有I/O请求时才启动一个线程进行处理。用户进程也需要时不时的询问IO操作是否就绪，这就要求用户进程不停的去询问。 

异步阻塞IO（Java NIO）：  
   此种方式下是指应用发起一个IO操作以后，不等待内核IO操作的完成，等内核完成IO操作以后会通知应用程序，这其实就是同步和异步最关键的区别，同步必须等待或者主动的去询问IO是否完成，那么为什么说是阻塞的呢？因为此时是通过select系统调用来完成的，而select函数本身的实现方式是阻塞的，而采用select函数有个好处就是它可以同时监听多个文件句柄（如果从UNP的角度看，select属于同步操作。因为select之后，进程还需要读写数据），从而提高系统的并发性！  

（Java AIO(NIO.2)）异步非阻塞IO:  
   在此种模式下，用户进程只需要发起一个IO操作然后立即返回，等IO操作真正的完成以后，应用程序会得到IO操作完成的通知，此时用户进程只需要对数据进行处理就好了，不需要进行实际的IO读写操作，因为真正的IO读取或者写入操作已经由内核完成了。  


#### BIO、NIO、AIO适用场景分析: 

BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序直观简单易理解。 

NIO方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，并发局限于应用中，编程比较复杂，JDK1.4开始支持。 

AIO方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用OS参与并发操作，编程比较复杂，JDK7开始支持。 


### NIO概述
- NIO主要有三大核心部分：Channel(通道)、Buffer(缓冲区)、Selector(选择区)

> 传统IO基于字节流和字符流进行操作，而NIO基于`Channel`(通道)和`Buffer`(缓冲区)进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。`Selector`(选择区)用于监听多个通道的事件（比如：连接打开，数据到达）。因此，单个线程可以监听多个数据通道。

- NIO和传统IO（一下简称IO）之间第一个最大的区别

> `IO`是面向流的，`NIO`是面向缓冲区的。 Java IO面向流意味着每次从流中读一个或多个字节，直至读取所有字节，它们没有被缓存在任何地方。此外，它不能前后移动流中的数据。如果需要前后移动从流中读取的数据，需要先将它缓存到一个缓冲区。NIO的缓冲导向方法略有不同。数据读取到一个它稍后处理的缓冲区，需要时可在缓冲区中前后移动。这就增加了处理过程中的灵活性。但是，还需要检查是否该缓冲区中包含所有您需要处理的数据。而且，需确保当更多的数据读入缓冲区时，不要覆盖缓冲区里尚未处理的数据。 

> `IO`的各种流是阻塞的。这意味着，当一个线程调用`read()` 或`write()`时，该线程被阻塞，直到有一些数据被读取，或数据完全写入。该线程在此期间不能再干任何事情了。`NIO`的非阻塞模式，使一个线程从某通道发送请求读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取。而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可以继续做其他的事情。非阻塞写也是如此。一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。 线程通常将非阻塞IO的空闲时间用于在其它通道上执行IO操作，所以一个单独的线程现在可以管理多个输入和输出通道（`channel`）。

#### Channel
> 国内大多翻译成“通道”。`Channel`和IO中的Stream(流)是差不多一个等级的。只不过Stream是单向的，譬如：InputStream,OutputStream.而`Channel`是双向的，既可以用来进行读操作，又可以用来进行写操作。NIO中的Channel的主要实现有：

- `FileChannel` 对应文件IO
- `DatagramChannel` 对应UDP
- `SocketChannel` 对应TCP（Server）
- `ServerSocketChannel` 对应TCP（Client）

#### Buffer
> `NIO`中的关键`Buffer`实现有：`ByteBuffer`, `CharBuffer`,`DoubleBuffer`,`FloatBuffer`,`IntBuffer`,`LongBuffer`,`ShortBuffer`，分别对应基本数据类型:`byte`,`char`,`double`,`float`,`int`,`long`,`short`。当然`NIO`中还有`MappedByteBuffer`, `HeapByteBuffer`, `DirectByteBuffer`等这里先不进行陈述。

#### Selector

> `Selector`运行单线程处理多个`Channel`，如果你的应用打开了多个通道，但每个连接的流量都很低，使用`Selector`就会很方便。例如在一个聊天服务器中。要使用`Selector`,得向Selector注册Channel，然后调用它的select()方法。这个方法会一直阻塞到某个注册的通道有事件就绪。一旦这个方法返回，线程就可以处理这些事件，事件的例子有如新的连接进来、数据接收等。

#### Buffer的使用
> 顾名思义：缓冲区，实际上是一个容器，一个连续数组。Channel提供从文件、网络读取数据的渠道，但是读写的数据都必须经过Buffer。如下图：

![image](http://mmbiz.qpic.cn/mmbiz_jpg/eZzl4LXykQyU6xXZEpTCibLMJTDl4BT7m16kicdjdQuMWibia1fQlIibzRwbx4pLWjEsFZvGXITzibTEVuoaUQ90k0oQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1)

##### 向Buffer中写数据

- 从Channel写到Buffer fileChannel.read(buf)
- 通过Buffer的put()方法 buf.put(param)

##### 从Buffer中读取数据

- 从Buffer读取到Channel channel.write(buf)
- 使用get()方法从Buffer中读取数据 buf.get()

可以把Buffer简单地理解为一组基本数据类型的元素列表，它通过几个变量来保存这个数据的当前位置状态：capacity, position, limit, mark：

索引 | 说明
---|---
capacity | 缓冲区数组的总长度
position | 缓冲区数组下一个操作元素的index
limit | 缓冲区数组下一个不可操作元素的index，limit <= capacity
mark | 默认为-1，或用于记录当前position的前一个位置

![image](http://mmbiz.qpic.cn/mmbiz_png/eZzl4LXykQyU6xXZEpTCibLMJTDl4BT7mwUl1p0iaKqh0Q3P740yKFiaEgIiaNE2fsTBwInLEYU8pb0LM1YTyPyLlQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

举例：我们通过ByteBuffer.allocate(11)方法创建了一个11个byte的数组的缓冲区，初始状态如上图，position的位置为0，capacity和limit默认都是数组长度。当我们写入5个字节时，变化如下图：

![image](http://mmbiz.qpic.cn/mmbiz_png/eZzl4LXykQyU6xXZEpTCibLMJTDl4BT7mMPNtyiaG1EOdn0OS5sdZ8QcTBI402mnRmFlb1s3COL2bZCStlNQRPQQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

这时我们需要将缓冲区中的5个字节数据写入Channel的通信信道，所以我们调用ByteBuffer.flip()方法，变化如下图所示(position设回0，并将limit设成之前的position的值)：

![image](http://mmbiz.qpic.cn/mmbiz_png/eZzl4LXykQyU6xXZEpTCibLMJTDl4BT7mbtgA4szQ4eT287bao430vFO1PgdNP1xD90EvMtBVaXSDFJXEITiawDQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

这时底层操作系统就可以从缓冲区中正确读取这个5个字节数据并发送出去了。在下一次写数据之前我们再调用clear()方法，缓冲区的索引位置又回到了初始位置。

调用clear()方法：position将被设回0，limit设置成capacity，换句话说，Buffer被清空了，其实Buffer中的数据并未被清楚，只是这些标记告诉我们可以从哪里开始往Buffer里写数据。如果Buffer中有一些未读的数据，调用`clear()`方法，数据将“被遗忘”，意味着不再有任何标记会告诉你哪些数据被读过，哪些还没有。如果Buffer中仍有未读的数据，且后续还需要这些数据，但是此时想要先先写些数据，那么使用`compact()`方法。`compact()`方法将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像`clear()`方法一样，设置成capacity。现在Buffer准备好写数据了，但是不会覆盖未读的数据。



通过调用`Buffer.mark()`方法，可以标记Buffer中的一个特定的position，之后可以通过调用`Buffer.reset()`方法恢复到这个position。`Buffer.rewind()`方法将position设回0，所以你可以重读Buffer中的所有数据。limit保持不变，仍然表示能从Buffer中读取多少个元素。

#### 读取文件

```java
 @Test
public void test1() throws Exception{
        RandomAccessFile randomAccessFile = new RandomAccessFile("c:/Users/huayu/Desktop/data参数中的json数据.txt","rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
       int bytesRead = fileChannel.read(byteBuffer);
        System.out.println(bytesRead);
        while (bytesRead != -1) {
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()){
                System.out.println((char) byteBuffer.get());
            }
            byteBuffer.compact();
            bytesRead = fileChannel.read(byteBuffer);
        }
}
```


#### SocketChannel

> 说完了FileChannel和Buffer,大家应该对Buffer的用法比较了解了，这里使用SocketChannel来继续探讨NIO。NIO的强大功能部分来自于Channel的非阻塞特性，套接字的某些操作可能会无限期地阻塞。例如，对accept()方法的调用可能会因为等待一个客户端连接而阻塞；对read()方法的调用可能会因为没有数据可读而阻塞，直到连接的另一端传来新的数据。总的来说，创建/接收连接或读写数据等I/O调用，都可能无限期地阻塞等待，直到底层的网络实现发生了什么。慢速的，有损耗的网络，或仅仅是简单的网络故障都可能导致任意时间的延迟。然而不幸的是，在调用一个方法之前无法知道其是否阻塞。NIO的channel抽象的一个重要特征就是可以通过配置它的阻塞行为，以实现非阻塞式的信道。


#### Selector

> 一个Selector实例可以同时检查一组信道的I/O状态。用专业术语来说，选择器就是一个多路开关选择器，因为一个选择器能够管理多个信道上的I/O操作。然而如果用传统的方式来处理这么多客户端，使用的方法是循环地一个一个地去检查所有的客户端是否有I/O操作，如果当前客户端有I/O操作，则可能把当前客户端扔给一个线程池去处理，如果没有I/O操作则进行下一个轮询，当所有的客户端都轮询过了又接着从头开始轮询；这种方法是非常笨而且也非常浪费资源，因为大部分客户端是没有I/O操作，我们也要去检查；而Selector就不一样了，它在内部可以同时管理多个I/O，当一个信道有I/O操作的时候，他会通知Selector，Selector就是记住这个信道有I/O操作，并且知道是何种I/O操作，是读呢？是写呢？还是接受新的连接；所以如果使用Selector，它返回的结果只有两种结果，一种是0，即在你调用的时刻没有任何客户端需要I/O操作，另一种结果是一组需要I/O操作的客户端，这是你就根本不需要再检查了，因为它返回给你的肯定是你想要的。这样一种通知的方式比那种主动轮询的方式要高效得多！


#### 简单事例
```java
public class Client {

	//需要一个Selector 
	public static void main(String[] args) {
		
		//创建连接的地址
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8765);
		
		//声明连接通道
		SocketChannel sc = null;
		
		//建立缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		try {
			//打开通道
			sc = SocketChannel.open();
			//进行连接
			sc.connect(address);
			
			while(true){
				//定义一个字节数组，然后使用系统录入功能：
				byte[] bytes = new byte[1024];
				System.in.read(bytes);
				
				//把数据放到缓冲区中
				buf.put(bytes);
				//对缓冲区进行复位
				buf.flip();
				//写出数据
				sc.write(buf);
				//清空缓冲区数据
				buf.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(sc != null){
				try {
					sc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
```

```java
public class Server implements Runnable {
    //1 多路复用器（管理所有的通道）
    private Selector seletor;
    //2 建立缓冲区
    private ByteBuffer readBuf = ByteBuffer.allocate(1024);
    //3
    private ByteBuffer writeBuf = ByteBuffer.allocate(1024);

    public Server(int port) {
        try {
            //1 打开路复用器
            this.seletor = Selector.open();
            //2 打开服务器通道
            ServerSocketChannel ssc = ServerSocketChannel.open();
            //3 设置服务器通道为非阻塞模式
            ssc.configureBlocking(false);
            //4 绑定地址
            //noinspection Since15
            ssc.bind(new InetSocketAddress(port));

            //5 把服务器通道注册到多路复用器上，并且监听阻塞事件
            ssc.register(this.seletor, SelectionKey.OP_ACCEPT);

            System.out.println("Server start, port :" + port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                //1 必须要让多路复用器开始监听
                this.seletor.select();
                //2 返回多路复用器已经选择的结果集
                Iterator<SelectionKey> keys = this.seletor.selectedKeys().iterator();
                //3 进行遍历
                while (keys.hasNext()) {
                    //4 获取一个选择的元素
                    SelectionKey key = keys.next();
                    //5 直接从容器中移除就可以了
                    keys.remove();
                    //6 如果是有效的
                    if (key.isValid()) {
                        //7 如果为阻塞状态
                        if (key.isAcceptable()) {
                            this.accept(key);
                        }
                        //8 如果为可读状态
                        if (key.isReadable()) {
                            this.read(key);
                        }
                        //9 写数据
                        if (key.isWritable()) {
                            //this.write(key); //ssc
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void write(SelectionKey key) {
        //ServerSocketChannel ssc =  (ServerSocketChannel) key.channel();
        //ssc.register(this.seletor, SelectionKey.OP_WRITE);
    }

    private void read(SelectionKey key) {
        try {
            //1 清空缓冲区旧的数据
            this.readBuf.clear();
            //2 获取之前注册的socket通道对象
            SocketChannel sc = (SocketChannel) key.channel();
            //3 读取数据
            int count = sc.read(this.readBuf);
            //4 如果没有数据
            if (count == -1) {
                key.channel().close();
                key.cancel();
                return;
            }
            //5 有数据则进行读取 读取之前需要进行复位方法(把position 和limit进行复位)
            this.readBuf.flip();
            //6 根据缓冲区的数据长度创建相应大小的byte数组，接收缓冲区的数据
            byte[] bytes = new byte[this.readBuf.remaining()];
            //7 接收缓冲区数据
            this.readBuf.get(bytes);
            //8 打印结果
            String body = new String(bytes).trim();
            System.out.println("Server : " + body);

            // 9..可以写回给客户端数据

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void accept(SelectionKey key) {
        try {
            //1 获取服务通道
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            //2 执行阻塞方法
            SocketChannel sc = ssc.accept();
            //3 设置阻塞模式
            sc.configureBlocking(false);
            //4 注册到多路复用器上，并设置读取标识
            sc.register(this.seletor, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        new Thread(new Server(8765)).start();
    }
}

```



#### 参考文献
http://blog.csdn.net/itismelzp/article/details/50886009

