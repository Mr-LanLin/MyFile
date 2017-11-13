### JavaIO

#### JavaIO概述

> Java IO 是一套Java用来读写数据（输入和输出）的API,主要涉及文件，网络数据流，内存缓冲等的输入输出。

#### 流

> 在Java IO中，流是一个核心的概念。流从概念上来说是一个连续的数据流。你既可以从流中读取数据，也可以往流中写数据。流与数据源或者数据流向的媒介相关联。在Java IO中流既可以是字节流(以字节为单位进行读写)，也可以是字符流(以字符为单位进行读写)。

#### IO相关的媒介

> Java的IO包主要关注的是从原始数据源的读取以及输出原始数据到目标媒介。以下是最典型的数据源和目标媒介：
* 文件
* 管道
* 网络连接
* 内存缓存
* ` System.in, System.out, System.error` (注：Java标准输入、输出、错误输出)

#### IO类集成关系

![image](http://img.blog.csdn.net/20150910154516952)

#### Java IO中各个类所负责的媒介

![image](http://img.blog.csdn.net/20150910154540639)

#### 流的分类

* 字节流，以字节为单位进行读写。字节流对应的类应该是`InputStream`和`OutputStream`。
* 字符流，以字符为单位进行读写。字符流对应的类应该是`Reader`和`Writer`。
* 转换流，字节流转换为字符流。通过`InputStreamReader`和`OutputStreamWriter`字节流可以转换成字符流。

#### 随机读写File文件

> 读取File文件可以用FileInputStream（文件字符流）或FileReader（文件字节流）来读文件，这两个类可以让我们分别以字符和字节的方式来读取文件内容，但是它们都有一个不足之处，就是只能从文件头开始读，然后读到文件结束。但是有时候我们只希望读取文件的一部分，或者是说随机的读取文件，那么我们就可以利用RandomAccessFile。RandomAccessFile提供了seek()方法，用来定位将要读写文件的指针位置，我们也可以通过调用getFilePointer()方法来获取当前指针的位置，具体看下面的例子:

##### 随机读文件
``` java
public static void randomAccessFileRead() throws IOException {
     // 创建一个RandomAccessFile对象
     RandomAccessFile file = new RandomAccessFile( "d:/test.txt", "rw");
     // 通过seek方法来移动读写位置的指针
     file.seek(10);
     // 获取当前指针
     long pointerBegin = file.getFilePointer();
     // 从当前指针开始读
     byte[] contents = new byte[1024];
     file.read( contents);
     long pointerEnd = file.getFilePointer();
     System. out.println( "pointerBegin:" + pointerBegin + "\n" + "pointerEnd:" + pointerEnd + "\n" + new String(contents));
     file.close();
}
```

##### 随机写文件

``` java
public static void randomAccessFileWrite() throws IOException {
     // 创建一个RandomAccessFile对象
     RandomAccessFile file = new RandomAccessFile( "d:/test.txt", "rw");
     // 通过seek方法来移动读写位置的指针
     file.seek(10);
     // 获取当前指针
     long pointerBegin = file.getFilePointer();
     // 从当前指针位置开始写
     file.write( "HELLO WORD".getBytes());
     long pointerEnd = file.getFilePointer();
     System. out.println( "pointerBegin:" + pointerBegin + "\n" + "pointerEnd:" + pointerEnd + "\n" );
     file.close();
}
````

#### 管道媒介
> 管道主要用来实现同一个虚拟机中的两个线程进行交流。因此，一个管道既可以作为数据源媒介也可作为目标媒介。需要注意的是java中的管道和Unix/Linux中的管道含义并不一样，在Unix/Linux中管道可以作为两个位于不同空间进程通信的媒介，而在java中，管道只能为同一个JVM进程中的不同线程进行通信。和管道相关的IO类为：PipedInputStream和PipedOutputStream。

``` java 
public class PipeExample {
   public static void main(String[] args) throws IOException {
      final PipedOutputStream output = new PipedOutputStream();
      final PipedInputStream  input  = new PipedInputStream(output);
      Thread thread1 = new Thread( new Runnable() {
          @Override
          public void run() {
              try {
                  output.write( "Hello world, pipe!".getBytes());
              } catch (IOException e) {
              }
          }
      });
      Thread thread2 = new Thread( new Runnable() {
          @Override
          public void run() {
              try {
                  int data = input.read();
                  while( data != -1){
                      System. out.print(( char) data);
                      data = input.read();
                  }
              } catch (IOException e) {
              } finally{
                try {
                       input.close();
                } catch (IOException e) {
                       e.printStackTrace();
                }
              }
          }
      });
      thread1.start();
      thread2.start();
   }
}
```
##### 未完待续...