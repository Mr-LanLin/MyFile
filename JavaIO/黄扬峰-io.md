# JAVA IO

## 字节流

>以字节的方式读取数据，可以对任何数据文件进行读取处理。
- inputstream、outputstream（以inputstream举例）
>fileInputstream：读取文件
>filterInputStream:装饰者，持有inputstream，子类扩充功能（bufferedinputstream、datainputstream）
- objectinputstream：序列化和反序列化
- SequenceInputStream：合并流，文件合并
- ByteArrayInputStream：带字节数组的缓冲流

## 字符流
>以字符的方式读取数据，只能读取以字符为单位的数据文件。
- reader
- writer

## RandomAccessFile随机读写
>实现了dataInput和dataoutput两个读取数据的接口，类可以获取文件大小等信息，通过seek方法将文件指针指到对应的位置

## FileDescriptor类
>文件描述，在操作中并不会操作这个类，这个类中记载了文件的信息。



## 备注
>一个字符是由1个或多个字节组成（由编码方式决定）