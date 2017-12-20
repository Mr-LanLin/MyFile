# Java注解

## 4个元注解

- @Target（注解修饰的范围）
>1.CONSTRUCTOR:用于描述构造器</br>
2.FIELD:用于描述域</br>
3.LOCAL_VARIABLE:用于描述局部变量</br>
4.METHOD:用于描述方法</br>
5.PACKAGE:用于描述包</br>
6.PARAMETER:用于描述参数</br>
7.TYPE:用于描述类、接口(包括注解类型) 或enum声明

- @Retention（注解保留时间）
>1.SOURCE:在源文件中有效（即源文件保留）</br>
2.CLASS:在class文件中有效（即class保留）</br>
3.RUNTIME:在运行时有效（即运行时保留）

- @Documented（写入javadoc文档）

- @Inherited（是否继承）

## 注解导图：
![image](http://images.cnitblog.com/blog/34483/201304/25200814-475cf2f3a8d24e0bb3b4c442a4b44734.jpg)


参考链接：
- [博客-注解基本](https://www.cnblogs.com/peida/archive/2013/04/24/3036689.html)
- [博客-实现讲解](http://www.importnew.com/24051.html)
- [知乎-注解的实现](https://www.zhihu.com/question/24401191)
- [知乎-理解运用](https://www.zhihu.com/question/47449512/answer/106034220)