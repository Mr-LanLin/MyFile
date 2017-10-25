# Java I/O系统

## I/O系统类图



## File类

File类能代表一个文件或目录。如果它指的是一个文件集，可以调用`list()`或`listFiles()`方法，返回一个数组。可以使用FilenameFilter来过滤想要的文件。也可以用来创建新的文件或目录，以及查看文件的信息`getName()`、`getPath()`、`length()`等。

```java
public class DirList {

    public static FilenameFilter filter(final String regex) {
        return new FilenameFilter() {
            //设置正则表达式过滤规则
            private Pattern pattern = Pattern.compile(regex);

            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        };
    }

    public static void main(String[] args) {
        File path = new File(".");
        File[] list;
        //保留文件名以.开头的
        list = path.listFiles(filter("^\\..*"));
        for (File dirItem : list) {
            System.out.println("fileName: " + dirItem.getName());
            System.out.println("size: " + dirItem.length());
        }
    }
}
//输出结果
fileName: .classpath
size: 588
fileName: .gitignore
size: 360
fileName: .project
size: 390
fileName: .settings
size: 0
```

## 输入和输出

编程语言的I/O类库中常使用流这个抽象概念，它代表任何有能力产出数据的数据源对象或者是有能力接收数据的接收端对象。“流”屏蔽了实际的I/O设备中处理数据的细节。

Java类库中的I/O类分成输入和输出两部分。任何自`InputStream`或`Reader`派生而来的类都含有名为`read()`的基本方法，用于读取单个字节或者字节数组。同样，任何自`OutputStream`或者`Writer`派生而来的类都含有名为`write()`的基本方法，用于写单个字节或者字节数组。但是，一般不会直接使用这些方法，而是使用他们的包装类。

### InputStream

InputStream用来表示那些从不同数据源产生输入的类，包括：字节数组、String对象、文件、“管到”、一个由其他种类的流组成的序列、其他数据源（如Internet连接）。

每种数据源都有对应的InputStream子类。FilterInputStream也属于一种InputStream，是“包装类”的基类。


类 | 功能 | 构造器参数
---|---|---
ByteArrayInputStream | 允许将内存的缓冲区当作InputStream使用 | 字节数组，字节从中取出。
StringBufferInputStream | 将String转成InputStream | 字符串。底层使用StringBuffer。
FileInputStream | 从文件中读取信息 | 字符串，表示文件名、文件或FileDescriptor对象。
PipedInputStream | 产生用于写入相关PipedOutputStream的数据。实现管道化 | PipedOutputStream
SequenceInputStream | 将两个或多个InputStream对象转换成单一InputStream | 两个InputStream对象或一个容纳InputStream对象的Enumeration容器
ObjectInputStream | （从文件中）读取对象 | InputStream
FilterInputStream | 抽象类，作为包装类的接口，为其他InputStream类提供有用供能 | InputStream

> 注：几乎所有的InputStream都能够与FilterInputStream连用以获得更强到的功能

### OutputStream

OutputStream决定了输出所要去往的目标：字节数组、文件或管到。同样FilterOutputStream也是继承自OutputStream，作为包装类的基类。


类 | 功能 | 构造器参数
---|---|---
ByteArrayOutputStream | 在内存中创建缓冲区。所有送往流的数据都要放在此缓冲区 | 缓冲区size（可选）
FileOutputStream | 将信息写到文件 | String，表示文件名、文件或FileDescriptor对象
PipedOutputStream | 任何写入其中的信息都会自动作为相关PipedInputStream的输出。实现管道化概念 | PipedInputStream
ObjectOutputStream | （向文件中）写出对象 | InputStream
FilterOutputStream | 抽象类，作为包装类的接口，为其他OutputStream提供有用功能 | OutputStream

> 注：几乎所有的OutputStream都能够与FilterOutputStream连用以获得更强到的功能

### 添加属性和有用的接口

FilterInputStream和FilterOutputStream是用来提供装饰器类接口以控制特定的InputStream和OutputStream的两个类。这两个类是装饰器的必要条件。

#### 通过FilterInputStream从InputStream读取数据

FilterInputStream能够完成两件不同的事。其中DataInputStream允许我们读取不同的基本类型数据以及String对象（如：readByte()、readFloat()等）。搭配相应的DataOutputStream，就可以通过数据流将基本类型的数据从一个地方迁移到另一个地方。

其他FilterInputStream类则在内部修改InputStream的行为方式：是否缓冲、是否保留它所读过的行以及是否把单一字符推回输入流等。不管正在连接的是什么I/O设备，几乎每次都要对输入进行缓冲，所以I/O类库把无缓冲输入作为特殊情况。

类 | 功能 | 构造器参数/如何使用
---|---|---
DataInputStream | 与DataOutputStream搭配使用，因此可以按照可移植的方式从流读取基本数据类型 | InputStream。包含用于读取基本类型数据的全部接口
BufferedIntputStream | 使用缓冲区，避免每次读取时都进行实际的写操作 | InputStream，可指定缓冲区size（可选）。本质上不提供接口，只不过是向进程中添加缓冲区所必需的，与接口对象搭配使用
LineNumberInputStream | 跟踪输入流中的行号，可调用getLineNumber()和setLineNumber(int) | InputStream。仅增加了行号，与接口对象搭配使用
PushbackInputStream | 具有能弹出一个字节的缓冲区，因此可以将读取到的最后一个字符回退 | InputStram。作为一种扫描器，并不常用
DigestInputStream | 通过读取输入流的方式完成摘要更新 | InputStream, MessageDigest（为应用程序提供信息摘要算法的功能）
CheckedInputStream | 需要维护所读取数据校验和的输入流 | InputStream, Checksum指定校验
DeflaterInputStream | 为使用 "deflate" 压缩格式压缩数据实现输入流过滤器 | InputStream, Deflater（压缩器，可选）, int（缓冲数组length，可选）
InflaterInputStream | 为解压 "deflate"压缩格式的数据实现流过滤器。它还用作其他解压缩过滤器（如 GZIPInputStream）的基础 | InputStream, Inflater（解压器，可选）, int（缓冲数组length，可选）

> 注：后面4种流，都是在读取过程中，调用相关对象的更新、校验、压缩、解压等功能。

#### 通过FilterOutputStream向OutputStream写入

与DataInputStream对应的是DataOutputStream，它可以将各种基本数据类型以及String对象格式化输出到流中，如此，任何机器上的DataInputStream都能快速读取它们（如：writeByte()、writeFloat()等）。

PrintStream最初的目的是为了可视化格式打印所有基本数据类型以及String。这和DataOutputStream不同，后者的目的是将数据元素置入流中，是DataInputStream能够可移植地重构它们。PrintStream有两个重要的方法：print()和println()，后者打印完后会换行。因为PrintStream捕获了所有IOException，因此需要checkError自行测试错误状态，出错返回true。

BufferedOutputStream是一个修改过的OutputStream，它对数据流使用缓冲技术，不用每次向流写入时都进行实际的物理动作。


类 | 功能 | 构造器参数/如何使用
---|---|---
DataOutputStream | 与DataInputStream搭配使用，因此可以按照可移植方式向流中写入基本类型数据 | OutputStream。包含用于写入基本类型的全部接口
PrintStream | 用于产生格式化输出。其中DataOutputStream处理数据存储，PrintStream处理显示 | OutputStream，可以用boolean值指示是否在每次换行时清空缓冲区（可选）
BufferedOutputStream | 使用缓冲区避免每次发送数据时都进行实际写操作。可以调用flush()清空缓冲区 | OutputStream, 缓冲区size（可选）。本质上不提供接口，只不过是向进程中添加缓冲区
DigestOutputStream | 摘要输出流，使用通过流的位更新关联消息摘要的透明流 | OutputStream, MessageDigest消息摘要
CheckedOutputStream | 需要维护写入数据校验和的输出流 | OutputStream, Checksum指定校验
DeflaterOutputStream | 为使用 "deflate" 压缩格式压缩数据实现输出流过滤器。它还用作其他类型的压缩过滤器（如 GZIPOutputStream）的基础 | OutputStream, Deflater（压缩器，可选）, int（缓冲数组length，可选）
InflaterOutputStream | 为解压缩 "deflate" 压缩格式存储的数据实现输出流过滤器 | OutputStream, Inflater（解压器，可选）, int（缓冲数组length，可选）

> 注：后面4种流，都是在写出后，调用相关对象的更新、校验、压缩、解压等功能。

### Reader和Writer

Reader和Writer提供了兼容Unicode与面向字符的I/O功能。Reader和Writer继承层次结构主要是为了国际化。老的I/O继承层次结构仅支持8为的字节流，并且不能很好地处理16位的Unicode字符（java本身的char也是16位的Unicode），所以添加Reader和Writer继承层次结构就是为了在所有I/O操作中都支持Unicode。有时需要字节流和字符流结合使用，这就需要用到“适配器”类:InputStreamReader（可以把InputStream转换为Reader）、OutputStreamWriter（可以吧OutputStream转换为Writer）。

#### 数据的来源和去处

几乎所有原始的java I/O流都有相应的Reader和Writer类来提供Unicode操作。然而在某些场合，面向字节的InputStream和OutputStream才是正确的解决方案。特别是java.util.zip类库就是面向字节的。字节流和字符流信息的来源和去处对应关系

字节流 | 对应的字符流
---|---
InputStream | Reader（适配器InputStreamReader）
OutputStream | Writer（适配器OutputStreamWriter）
FileInputStream | FileReader
FileOutputStream | FileWriter
StringBufferInputStream（已弃用）| StringReader
无 | StringWriter
ByteArrayInputStream | CharArrayReader
ByteArrayOutputStream | CharArrayWriter
PipedInputStream | PipedReader
PipedOutputStream | PipedWriter

对于InputStream和OutputStream来说。使用FilterInputStream和FilterOutputSteam的装饰器子类来修改流以满足特殊需要。Reader和Writer的类继承层次结构也沿用了类似的思想。

### RandomAccessFile

RandomAccessFile继承自Object，实现了DataInput和DataOutput，它是一个相对独立的类。适用于由大小已知的记录组成的文件，所以可以使用seek()将记录从一处转移到另一处，然后读取或者修改记录。getFilePointer()用于查找当前所处的文件位置，seek()用于在文件内移至新的位置，length()用于判断文件的最大尺寸，另外构造器第二个参数还可以指定读写规则："r"读、"rw"读写

### 典型的使用方式

几种常见的I/O流组合方式

#### 缓冲输入文件

打开一个文件用于字符输入，可以使用以String或File对象作为文件名的FileInputReader。为了提高速度需要对文件进行缓冲。

```java
public class BufferedInputFile {
    public static String read(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String s;
        StringBuilder sb = new StringBuilder();
        while((s = in.readLine() != null){
            sb.append("s" + "\n");
        }
        in.close();
        return sb.toString();
    }
}
```

#### 从内存读取

从BufferedInputFile.read()读入的String结果用来创建一个StringReader，然后调用read()每次读取一个字节。

```java
public class MemoryInput {
    public static void main(String[] args) throw IOException {
        StringReader in = new StringReader(BufferedInputFile.read("MemoryInput.java"));
        int c;
        while((c = in.read()) != -1){
            System.out.print((char) c);
        }
    }
}
```

#### 格式化的内存输入

要读取格式化数据，可以使用DataInputStream。它是一个面向字节的I/O类，可以用InputStream以字节的形式读取任何数据。

```java
public class FormattedMemoryInput {
    public static void main(String[] args) throw IOException {
        try{
            DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(
                    BufferedInputFile.read("FormattedMemoryInput.java").getBytes()));
            while(true) {
                System.out.print((char) in.readByte());
            }
        } catch (EOFException e) {
            System.err.println("End of stream");
        }
    }
}
```

#### 基本的文件输出

FileWrite对象可以向文件写入数据，通常会使用BufferedWriter将其包装。

```java
public class BasicFileOutput {
    static String file = "BasicFileOutput.out";
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new StringReader(
            BufferedInputFile.read("BasicFileOutput.java").getBytes()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        int lineCount = 1;
        String s;
        while((s = in.readLine()) != null){
            out.println(lineCount++ + ":" + s);
        }
        out.close();
        System.out.print(BufferedInputFile.read(file));
    }
}
```

#### 存储和恢复数据

PrintWriter可以对数据进行格式化，DataOutputStream可以写入数据，并且用DataInputStream恢复数据。`writeUTF()`和`readUTF()`，可以把字符串和其他数据类型混合，并且可以很容易使用DataInputStream来恢复它。无关平台，无关类型。

```java
public static void main(String[] args) throws IOException {
    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("Data.txt")));
    out.writeDouble(3.14);
    out.wirteUTF("this is pi");
    out.close();
    DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("Data.txt")));
    System.out.print(in.readDouble());
    System.out.print(in.readUTF());
}
```

#### RandomAccessFile

RandomAccessFile类似于结合了DataInputStream和DataOutputStream（实现了相同的接口：DataInput和DataOutput）。使用RandomAccessFile时必须知道文件排版，才能正确操作它。RandomAccessFile拥有读取基本类型和UTF-8字符串的各种具体方法。

#### 管道流

因为管道流用于任务间的通信，因此要结合多线程才能彰显其真正的价值

### 标准I/O

程序的所有输入都来自于标准输入，所有输出都来自于标准输出。一个程序的标准输出可以成另一个程序的标准输入

按照标准IO模型，Java提供了System.in、System.out和System.err。System.out和System.err都是PrintStream对象，System.in则是一个未加工的InputStream。使用System.in前应该对其进行包装。

Java的System提供了一些方法可以对标准流进行重定向：`setIn(InputStream)`、`setOut(PrintStream)`、`setErr(PrintStream)`


### 总结

- javaI/O流类库能满足我们的基本需求：通过控制台、文件、内存甚至Internet进行读写。
- 除了基本操作外，还提供了对消息摘要、文件压缩、xml、对象序列化等功能的支持
- 使用装饰器模式，可以很方便添加新的功能，但是会造成包装的层次过多，经常为了获得一个类，而创建很多新的类

