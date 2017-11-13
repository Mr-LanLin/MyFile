## 一、基本概念描述

### 1.1 I/O简介

I/O即输入输出，是计算机与外界世界的一个借口。IO操作的实际主题是操作系统。在java编程中，一般使用流的方式来处理IO，所有的IO都被视作是单个字节的移动，通过stream对象一次移动一个字节。流IO负责把对象转换为字节，然后再转换为对象。



### 1.2 什么是NIO

**_NIO_**即New IO，这个库是在JDK1.4中才引入的。NIO和IO有相同的作用和目的，但实现方式不同，**_NIO主要用到的是块_**，所以NIO的效率要比IO高很多。

在Java API中提供了两套NIO，一套是针对**标准输入输出NIO**，另一套就是**网络编程NIO**。

### 1.3 流与块的比较

NIO和IO最大的区别是数据打包和传输方式。IO是以**流**的方式处理数据，而NIO是以**块**的方式处理数据。

**面向流**的IO一次一个字节的处理数据，一个输入流产生一个字节，一个输出流就消费一个字节。为流式数据创建过滤器就变得非常容易，链接几个过滤器，以便对数据进行处理非常方便而简单，但是面向流的IO通常处理的很慢。

**面向块**的IO系统以块的形式处理数据。每一个操作都在一步中产生或消费一个数据块。按块要比按流快的多，但面向块的IO缺少了面向流IO所具有的有雅兴和简单性。

## 二、NIO基础

**Buffer**和**Channel**是标准NIO中的核心对象，几乎每一个IO操作中都会用到它们。

Channel是对原IO中流的模拟，任何来源和目的数据都必须通过一个Channel对象。一个Buffer实质上是一个容器对象，发给Channel的所有对象都必须先放到Buffer中；同样的，从Channel中读取的任何数据都要读到Buffer中。

### 2.1 关于Buffer

**Buffer**是一个对象，它包含一些要写入或读出的数据。在NIO中，数据是放入buffer对象的，而在IO中，数据是直接写入或者读到Stream对象的。**_应用程序不能直接对 Channel 进行读写操作，而必须通过 Buffer 来进行_**，即 Channel 是通过 Buffer 来读写数据的。

在NIO中，所有的数据都是用Buffer处理的，它是NIO读写数据的中转池。Buffer实质上是一个数组，通常是一个字节数据，但也可以是其他类型的数组。但一个缓冲区不仅仅是一个数组，重要的是它提供了对数据的结构化访问，而且还可以跟踪系统的读写进程。

使用 Buffer 读写数据一般遵循以下四个步骤：

1.  写入数据到 Buffer；
2.  调用 flip() 方法；
3.  从 Buffer 中读取数据；
4.  调用 clear() 方法或者 compact() 方法。

当向 Buffer 写入数据时，Buffer 会记录下写了多少数据。一旦要读取数据，需要通过 flip() 方法将 Buffer **_从写模式切换到读模式_**。在读模式下，可以读取之前写入到 Buffer 的所有数据。

一旦读完了所有的数据，就需要清空缓冲区，让它可以再次被写入。有两种方式能清空缓冲区：调用 clear() 或 compact() 方法。clear() 方法会清空整个缓冲区。compact() 方法只会清除已经读过的数据。任何未读的数据都被移到缓冲区的起始处，新写入的数据将放到缓冲区未读数据的后面。

Buffer主要有如下几种：

![这里写图片描述](http://www.ibm.com/developerworks/cn/java/j-lo-io-optimize/img001.jpg)

### 2.3 关于Channel

**Channel**是一个对象，可以通过它读取和写入数据。可以把它看做IO中的流。但是它和流相比还有一些不同：

1.  Channel是双向的，既可以读又可以写，而流是单向的
2.  Channel可以进行异步的读写
3.  对Channel的读写必须通过buffer对象

正如上面提到的，所有数据都通过Buffer对象处理，所以，您永远不会将字节直接写入到Channel中，相反，您是将数据写入到Buffer中；同样，您也不会从Channel中读取字节，而是将数据从Channel读入Buffer，再从Buffer获取这个字节。

因为Channel是双向的，所以Channel可以比流更好地反映出底层操作系统的真实情况。特别是在Unix模型中，底层操作系统通常都是双向的。

![这里写图片描述](http://tutorials.jenkov.com/images/java-nio/overview-channels-buffers.png)

在Java NIO中Channel主要有如下几种类型：

*   FileChannel：从文件读取数据的
*   DatagramChannel：读写UDP网络协议数据
*   SocketChannel：读写TCP网络协议数据
*   ServerSocketChannel：可以监听TCP连接

## 三、从理论到实践：NIO中的读和写

IO中的读和写，对应的是数据和Stream，NIO中的读和写，则对应的就是通道和缓冲区。NIO中从通道中读取：创建一个缓冲区，然后让通道读取数据到缓冲区。NIO写入数据到通道：创建一个缓冲区，用数据填充它，然后让通道用这些数据来执行写入。

### 3.1 从文件中读取

我们已经知道，在NIO系统中，任何时候执行一个读操作，您都是从Channel中读取，而您不是直接从Channel中读取数据，因为所有的数据都必须用Buffer来封装，所以您应该是从Channel读取数据到Buffer。

因此，如果从文件读取数据的话，需要如下三步：

1.  从FileInputStream获取Channel
2.  创建Buffer
3.  从Channel读取数据到Buffer

下面我们看一下具体过程：
**_第一步：获取通道_**

```
FileInputStream fin = new FileInputStream( "readandshow.txt" );
FileChannel fc = fin.getChannel();  

```

**_第二步：创建缓冲区_**

```
ByteBuffer buffer = ByteBuffer.allocate( 1024 );

```

**_第三步：将数据从通道读到缓冲区_**

```
fc.read( buffer );

```

### 3.2 写入数据到文件

类似于从文件读数据，
**_第一步：获取一个通道_**

```
FileOutputStream fout = new FileOutputStream( "writesomebytes.txt" );
FileChannel fc = fout.getChannel();

```

**_第二步：创建缓冲区，将数据放入缓冲区_**

```
ByteBuffer buffer = ByteBuffer.allocate( 1024 );

for (int i=0; i<message.length; ++i) {
 buffer.put( message[i] );
}
buffer.flip();

```

**_第三步：把缓冲区数据写入通道中_**

```
fc.write( buffer );

```

### 3.3 读写结合

CopyFile是一个非常好的读写结合的例子，我们将通过CopyFile这个实力让大家体会NIO的操作过程。CopyFile执行三个基本的操作：创建一个Buffer，然后从源文件读取数据到缓冲区，然后再将缓冲区写入目标文件。

```
/**
 * 用java NIO api拷贝文件
 * @param src
 * @param dst
 * @throws IOException
 */
public static void copyFileUseNIO(String src,String dst) throws IOException{
    //声明源文件和目标文件
            FileInputStream fi=new FileInputStream(new File(src));
            FileOutputStream fo=new FileOutputStream(new File(dst));
            //获得传输通道channel
            FileChannel inChannel=fi.getChannel();
            FileChannel outChannel=fo.getChannel();
            //获得容器buffer
            ByteBuffer buffer=ByteBuffer.allocate(1024);
            while(true){
                //判断是否读完文件
                int eof =inChannel.read(buffer);
                if(eof==-1){
                    break;  
                }
                //重设一下buffer的position=0，limit=position
                buffer.flip();
                //开始写
                outChannel.write(buffer);
                //写完要重置buffer，重设position=0,limit=capacity
                buffer.clear();
            }
            inChannel.close();
            outChannel.close();
            fi.close();
            fo.close();
}     

```

## 四、需要注意的点

上面程序中有三个地方需要注意

### 4.1 检查状态

当没有更多的数据时，拷贝就算完成，此时 read() 方法会返回 -1 ，我们可以根据这个方法判断是否读完。

```
int r= fcin.read( buffer );
if (r==-1) {
     break;
     }

```

### 4.2 Buffer类的flip、clear方法

#### 控制buffer状态的三个变量

*   position：跟踪已经写了多少数据或读了多少数据，它指向的是下一个字节来自哪个位置
*   limit：代表还有多少数据可以取出或还有多少空间可以写入，它的值小于等于capacity。
*   capacity：代表缓冲区的最大容量，一般新建一个缓冲区的时候，limit的值和capacity的值默认是相等的。

flip、clear这两个方法便是用来设置这些值的。

#### **flip方法**

我们先看一下flip的源码：

```
public final Buffer flip() {
    limit = position;
    position = 0;
    mark = -1;
    return this;
 }

```

![这里写图片描述](http://img.blog.csdn.net/20150902152400081)

在上面的FileCopy程序中，写入数据之前我们调用了`buffer.flip();`方法，这个方法把当前的指针位置position设置成了limit，再将当前指针position指向数据的最开始端，我们现在可以将数据从缓冲区写入通道了。 position 被设置为 0，这意味着我们得到的下一个字节是第一个字节。 limit 已被设置为原来的 position，这意味着它包括以前读到的所有字节，并且一个字节也不多。

#### **clear方法**

先看一下clear的源码：

```
 public final Buffer clear() {
    position = 0;
    limit = capacity;
    mark = -1;
    return this;
}

```

![这里写图片描述](http://img.blog.csdn.net/20150902153303447)

在上面的FileCopy程序中，写入数据之后也就是读数据之前，我们调用了 `buffer.clear();`方法，这个方法重设缓冲区以便接收更多的字节。上图显示了在调用 clear() 后缓冲区的状态。

