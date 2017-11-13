# IO

###  一、概念
- 流就是字节序列的抽象概念，能被连续读取数据的数据源和能被连续写入数据的接收端就是流，流机制是Java及C++中的一个重要机制，通过流我们可以自由地控制文件、内存、IO设备等数据的流向。而IO流就是用于处理设备上的数据，如：硬盘、内存、键盘录入等。IO流根据处理类型的不同可分为字节流和字符流，根据流向的不同可分为输入流和输出流。

###  二、字节流和字符流的区别
- 字符流，因为文件编码的不同，就有了对字符进行高效操作的字符流对象，它的原理就是基于字节流读取字节时去查了指定的码表。它和字节流的区别有两点：1.在读取数据的时候，字节流读到一个字节就返回一个字节，字符流使用了字节流读到一个或多个字节（一个中文对应的字节数是两个，在UTF-8码表中是3个字节）时，先去查指定的编码表，再将查到的字符
返回；
- 字节流可以处理所有类型的数据，如jpg、avi、mp3、wav等等，而字符流只能处理字符数据。所以可以根据处理的文件不同考虑使用字节流还是字符流，如果是纯文本数据可以优先考虑字符流，否则使用字节流。

###  三、IO继承体系

![IO继承体系](http://open.thunisoft.com/chenjiayin/probe/raw/master/%E5%AD%A6%E4%B9%A0%E5%88%86%E4%BA%AB/Java%E7%9F%A5%E8%AF%86/JavaIO/%E5%BC%A0%E5%AE%8F%E4%BA%91-io/IO%E7%BB%A7%E6%89%BF%E4%BD%93%E7%B3%BB.png)
#### IO流主要可以分为节点流和处理流两大类
##### 1.节点流类型
 该类型可以从或者向一个特定的地点或者节点读写数据。主要类型如下：
 
![IO节点流](http://open.thunisoft.com/chenjiayin/probe/raw/master/%E5%AD%A6%E4%B9%A0%E5%88%86%E4%BA%AB/Java%E7%9F%A5%E8%AF%86/JavaIO/%E5%BC%A0%E5%AE%8F%E4%BA%91-io/%E8%8A%82%E7%82%B9%E6%B5%81.png)

#### 2.处理流类型
该类型是对一个已存在的流的连接和封装，通过所封装的流的功能调用实现数据读写，处理流的构造方法总是要带一个其他流对象作为参数，一个流对象进过其他流的多次包装，叫做流的链接。主要可以分为以下几种：

(1) 缓冲流（`BufferedInPutStream/BufferedOutPutStream和BufferedWriter/BufferedReader`）他可以提高对流的操作效率。
 写入缓冲区对象：                 
 BufferedWriter bufw=new BufferedWriter(new FileWriter("buf.txt"));
读取缓冲区对象：                
 BufferedReader bufr=new BufferedReader(new FileReader("buf.txt"));
 
` 注意：`该类型的流有一个特有的方法：readLine()；一次读一行，到行标记时，将行标记之前的字符数据作为字符串返回，当读到末尾时，返回null，其原理还是与缓冲区关联的流对象的read方法，只不过每一次读取到一个字符，先不进行具体操作，先进行临时储存，当读取到回车标记时，将临时容器中储存的数据一次性返回。

(2) 转换流（`InputStreamReader/OutputStreamWriter`）

- 该类型时字节流和字符流之间的桥梁，该流对象中可以对读取到的字节数据进行指定编码的编码转换。

##### 构造函数主要有：    

```java
 InputStreamReader(InputStream); //通过构造函数初始化，使用的是本系统默认的编码表GBK。  
 InputStreamWriter(InputStream,String charSet);//通过该构造函数初始化，可以指定编码表。  
 OutputStreamWriter(OutputStream); //通过该构造函数初始化，使用的是本系统默认的编码表GBK。  
 OutputStreamwriter(OutputStream,String charSet); //通过该构造函数初始化，可以指定编码表。 
 
```
   ` 注意：`在使用FileReader操作文本数据时，该对象使用的时默认的编码表，即

 FileReader fr=new FileReader(“a.txt”);  
  与   InputStreamReader isr=new InputStreamReader(new FileInputStream("a.txt"));   的意义相同。如果要使用指定表编码表时，必须使用转换流，即如果a.txt中的文件中的字符数据是通过utf-8的形式编码，那么在读取时，就必须指定编码表，那么转换流时必须的。即：
```java
 InputStreamReader isr=new InputStreamReader(new FileInputStream("a.txt"),utf-8);
```
  (3) 数据流（`DataInputStream/DataOutputStream`）
- 该数据流可以方便地对一些基本类型数据进行直接的存储和读取，不需要再进一步进行转换，通常只要操作基本数据类型的数据，就需要通过DataStream进行包装。
构造方法：  

```java
 DataInputStreamReader(InputStream);
 DataInputStreamWriter(OutputStream);
```

##### 方法举例：
 
```java
//一次读取四个字节，并将其转成int值           
  int readInt();
//一次写入四个字节，注意和write(int)不同，write(int)只将该整数的最低一个8位写入，
剩余三个8为丢失  
  writeInt(int);
  hort readShort();  
  writeShort(short);
//按照utf-8修改版读取字符，注意，它只能读writeUTF()写入的字符数据  
 String readUTF();
//按照utf-8修改版将字符数据进行存储，只能通过readUTF读取
 writeUTF(String);  
```

  `注意：`在使用数据流读/存数据的时候，需要有一定的顺序，即某个类型的数据先写入就必须先读出，服从先进先出的原则。

(4) 打印流（`PrintStream/PrintWriter`）
- ` PrintStream`是一个字节打印流，System.out对应的类型就是PrintStream，它的构造函数可以接受三种数据类型的值：字符串路径、File对象 、OutputStream；
-  `PrintStream`是一个字符打印流，它的构造函数可以接受四种类型的值：字符串路径、File对象、
OutputStream；

 (5) 对象流（`ObjectInputStream/ObjectOutputStream`）

   该类型的流可以把类作为一个整体进行存取，主要方法有：
```java
 //该方法抛出异常：ClassNotFountException
  Object readObject();
 //被写入的对象必须实现一个接口：Serializable，否则就会抛出：NotSerializableException
  void writeObject(Object);
```
### 4.IO流简单示例(`FileInputStream`)

```java
 public void FileInputStreamTest() throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis =new FileInputStream("D:/study/iotest/src.txt");
            fos =new FileOutputStream("D:/study/iotest/dest.txt");
            
            int x;
            while ((x = fis.read()) != -1) {
                fos.write(x);
            }
        }finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } finally {
                if (fos != null) {
                    fos.close();
              }
	 }
    }
}
```

