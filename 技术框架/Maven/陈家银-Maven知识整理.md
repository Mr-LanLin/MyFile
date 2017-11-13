# Maven相关考题

标签： Maven java

---

## 1. Maven命令相关

### (1). 基本命令

- archetype:generate 创建项目
- compile 编译
- test 测试
- package 打包
- install 安装
- deploy 发布
- site 生成站点目录
- clean 清理
- help 帮助
- version 查看版本

### (2). 高级命令

- mvn eclipse:eclipse 生成Eclipse项目结构
- mvn eclipse:eclipse -Dwtpversion=2.0 生成支持web的Eclipse项目结构
- mvn eclipse:clean 清除Eclipse项目结构
- mvn idea:idea 生成idea项目
- mvn package -Dmaven.test.skip=ture 打包时跳过测试
- mvn help:effective-pom 查看实际pom信息
- mvn dependency:tree 分析项目的依赖信息
- mvn <plug-in>:help 查看插件的帮助信息
- mvn jar:jar 只打jar包
- mvn tomcat:run 启动tomcat
- mvn jetty:run 启动jetty

### (3). 关键词

- Project:任何你想 build 的事物，Maven都会把它们当作是一个 Project。这些 Project 被定义为 POM(Project Object Model)。一个 Project 可以依赖其他的project，一个 project 也可以有多个子project组成。
- POM：POM(pom.xml) 是 Maven 的核心文件，它是指示Maven如何工作的元数据文件，类似 ant 的 build.xml 文件。pom.xml 文件应该位于每个 Project 的根目录。
- GroupId:顾名思义，这个应该是公司名或组织名。
- ArtifactId：构建出来的文件名，一般来说或，这个也是project名。
- Packaging：项目打包的类型，可以是将jar、war、rar、ear、pom，默认是jar。
- Version：项目的版本，项目的唯一标识由groupId+artifactId+packaging+versionz组成。
- Dependency:为了能够 build 或运行，一个典型的java project会依赖其他的包，在Maven中，这些被依赖的包就被称为 dependency。
- Plug-in：Maven是有插件组织的，它的每一个功能都是由插件提供的，主要的插件是由 java 来写的，但是他也支持 beanshell 和 ant 脚本编写的插件。
- Repository：仓库用来存放artifact的，可以是本地仓库，也可以是远程仓库，Maven是由一个默认的仓库。
- Snapshot：工程中可以（也应该）有这样一个特殊的版本：这个版本可以告诉Maven，该工程正在处于开发阶段，会经常更新（但还为发布）。当其他工程依赖此类型的artifact时，Maven会在仓库中寻找该artifact的最新版本，并自动下载、使用该最新版本。

## 2. Maven创建Java项目

```bash
mvn archetype:generate
-DgroupId=com.companyname.bank
-DartifactId=consumerBanking
-DarchetypeArtifactId=maven-archetype-quickstart
-DinteractiveMode=false
```

## 3. 什么是pom

Project Object Model，项目对象模型。通过xml表示maven项目，使用pom.xml来实现。主要描述了项目：包括配置文件；开发者需要遵循的规则，缺陷管理系统，组织和licenses，项目的url，项目的依赖性，以及其他所有的项目相关因素。

## 4. Maven标准的项目结构

![Maven结构][1]

## 5. Maven 生命周期

在Maven2中有了明确的生命周期概念，而且都提供与之对应的命令，使得项目构建更加清晰明了。主要23个生命周期阶段，以下列出主要的部分：

- validate，验证工程是否正确，所有需要的资源是否可用。
- compile，编译项目的源代码。
- test-compile，编译项目测试代码。
- test，使用已编译的测试代码，测试已编译的源代码。
- package，已发布的格式，如jar，将已编译的源代码打包。
- integration-test，在集成测试可以运行的环境中处理和发布包。
- verify，运行任何检查，验证包是否有效且达到质量标准。
- install，把包安装在本地的repository中，可以被其他工程作为依赖来使用
- deploy，在整合或者发布环境下执行，将最终版本的包拷贝到远程的repository，使得其他的开发者或者工程可以共享。
- generate-sources，产生应用需要的任何额外的源代码，如xdoclet。
如果要执行项目编译，那么直接输入：`mvn compile`即可，对于其他的阶段可以类推。阶段之间是存在依赖关系（dependency）的，如test依赖`test-compile`。在执行mvn test时，会先运行`mvn test-compile`，然后才是`mvn test`。

## 6. Maven的坐标及依赖

### (1). 坐标解释

Maven的坐标为各种构件引入了秩序，任何一个构件都必须明确的定义自己的坐标，maven的坐标包括如下的元素：

- groupId: 定义当前Maven项目隶属的实际项目
- artifactId: 该元素定义实际项目中的一个Maven项目或模块
- version: 该元素定义Maven项目当前所处的版本
- packaging: 该元素定义Maven项目的打包方式
- classifier: 该元素用来帮助定义构建输出的一些附属构件

> **注**：groupId、artifactId、version、packaging是必须定义的，classifier是不能被直接定义的，因为附属构件不是项目直接默认生成的，而是由附加的插件帮助生成的。

### (2). 依赖示例

```xml
<dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
 </dependencies>
```

### (3). 元素详解

根元素project下的含dependencies元素,dependencies可以包含一个或者多个dependency元素，以声明一个或多个项目依赖, 其包含的元素：

- groupId、artifactId、version：依赖的基本坐标，对于任何一个依赖来说，基本的坐标是最重要的，Maven是根据坐标来找到需要的依赖
- type: 依赖的类型
- scope: 依赖的范围
- optional: 标记依赖是否可选(参见可选性依赖)
- exclusions: 用来排除传递性依赖(参见依赖的传递性)

### (4). Maven的6种依赖范围

- compile: 编译依赖范围(默认)，对于编译、测试、运行三种classpath都有效
- test: 测试依赖范围， 只对测试classpath有效。典型范例：Junit
- provided: 已提供依赖范围 对于编译和测试classpath有效，但在运行时无效。典型范例：servlet-api
- runtime: 运行时依赖范围 对于测试和运行classpath有效，但在对编译主代码时无效。典型范例：JDBC
- system: 系统依赖范围
- import: (maven2.0.9及以上): 导入依赖范围，它不会对三种实际的classpath产生影响

### (5). 可选依赖

有时候我们不想让依赖传递，那么可配置该依赖为可选依赖，将元素optional设置为true即可,例如：

```xml
<dependency>
  <groupId>commons-logging</groupId>
  <artifactId>commons-logging</artifactId>
  <version>1.1.1</version>
  <optional>true<optional>
</dependency>
```

### (6). 传递依赖

可以使用`exclusions`元素声明排除依赖，exclusions可以包含一个或者多个exclusion子元素，因此可以排除一个或者多个传递性依赖。需要注意的是，声明exclusions的时候只需要groupId和artifactId，而不需要version元素，这是因为只需要groupId和artifactId就能唯一定位依赖图中的某个依赖。换句话说，Maven解析后的依赖中，不可能出现groupId和artifactId相同，但是version不同的两个依赖。如下是一个排除依赖的例子：

```xml
<dependency>
     <groupId>org.springframework</groupId>
     <artifactId>spring-core</artifactId>
     <version>2.5.6</version>
     <exclusions>
           <exclusion>
                <groupId>commons-logging</groupId>
                <artifactId>commons-logging</artifactId>
           </exclusion>
     </exclusions>
</dependency>
```

### (7). 依赖归类

如果我们项目中用到很多关于Spring Framework的依赖，它们分别是org.springframework:spring-core:2.5.6, org.springframework:spring-beans:2.5.6,org.springframework:spring-context:2.5.6,它们都是来自同一项目的不同模块。因此，所有这些依赖的版本都是相同的，而且可以预见，如果将来需要升级`Spring Framework`，这些依赖的版本会一起升级。因此，我们应该在一个唯一的地方定义版本，并且在dependency声明引用这一版本，这一在Spring Framework升级的时候只需要修改一处即可。

```xml
<properties>
    <springframework.version>2.5.6</springframework.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
        <version>${springframework.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-beans</artifactId>
        <version>${springframework.version}</version>  
    </dependency>
</dependencies>
```

## 7. maven依赖包查询下载顺序

Maven的依赖库查询顺序更改为：

1. 在 Maven 本地资源库中搜索，如果没有找到，进入第 2 步，否则退出。
2. 在 Maven 中央存储库搜索，如果没有找到，进入第 3 步，否则退出。
3. 在java.net Maven的远程存储库搜索，如果没有找到，提示错误信息，否则退出。

## 8. maven添加远程仓库

例如:添加JBoss远程仓库的详细信息在 “pom.xml” 文件中:

```xml
<project>
    <repositories>
        <repository>
            <id>JBoss repository</id>
            <url>http://repository.jboss.org/nexus/content/groups/public/</url>
        </repository>
    </repositories>
</project>
```

## 9. maven 相关配置

settings.xml中主要包括以下元素：

- localRepository：表示Maven用来在本地储存信息的本地仓库的目录。默认是用户家目录下面的.m2/repository目录。

- interactiveMode：表示是否使用交互模式，默认是true；如果设为false，那么当Maven需要用户进行输入的时候，它会使用一个默认值。

- offline：表示是否离线，默认是false。这个属性表示在Maven进行项目编译和部署等操作时是否允许Maven进行联网来下载所需要的信息。

- pluginGroups：在pluginGroups元素下面可以定义一系列的pluginGroup元素。表示当通过plugin的前缀来解析plugin的时候到哪里寻找。pluginGroup元素指定的是plugin的groupId。默认情况下，Maven会自动把org.apache.maven.plugins和org.codehaus.mojo添加到pluginGroups下。

- proxies：其下面可以定义一系列的proxy子元素，表示Maven在进行联网时需要使用到的代理。当设置了多个代理的时候第一个标记active为true的代理将会被使用。下面是一个使用代理的例子：

```xml
<proxies>
    <proxy>
        <id>xxx</id>
        <active>true</active>
        <protocol>http</protocol>
        <username>用户名</username>
        <password>密码</password>
        <host>代理服务器地址</host>
        <port>代理服务器的端口</port>
        <nonProxyHosts>不使用代理的主机</nonProxyHosts>
    </proxy>
</proxies>
```

- servers：其下面可以定义一系列的server子元素，表示当需要连接到一个远程服务器的时候需要使用到的验证方式。这主要有username/password和privateKey/passphrase这两种方式。以下是一个使用servers的示例：

```xml
<servers>
    <server>
        <id>id</id>
        <username>用户名</username>
        <password>密码</password>
    </server>
</servers>
```

- mirrors：用于定义一系列的远程仓库的镜像。我们可以在pom中定义一个下载工件的时候所使用的远程仓库。但是有时候这个远程仓库会比较忙，所以这个时候人们就想着给它创建镜像以缓解远程仓库的压力，也就是说会把对远程仓库的请求转换到对其镜像地址的请求。每个远程仓库都会有一个id，这样我们就可以创建自己的mirror来关联到该仓库，那么以后需要从远程仓库下载工件的时候Maven就可以从我们定义好的mirror站点来下载，这可以很好的缓解我们远程仓库的压力。在我们定义的mirror中每个远程仓库都只能有一个mirror与它关联，也就是说你不能同时配置多个mirror的mirrorOf指向同一个repositoryId。

看以下是一个使用mirrors的例子：

```xml
<mirrors>
    <mirror>
        <id>mirrorId</id>
        <mirrorOf>repositoryId</mirrorOf>
        <name>定义一个容易看懂的名称 </name>
        <url>http://my.repository.com/repo/path</url>
    </mirror>
</mirrors>
```

- profiles：用于指定一系列的profile。profile元素由activation、repositories、pluginRepositories和properties四个元素组成。当一个profile在settings.xml中是处于活动状态并且在pom.xml中定义了一个相同id的profile时，settings.xml中的profile会覆盖pom.xml中的profile。

## 10. 其他知识点

### (1). Maven安装插件

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-source-plugin</artifactId>
            <version>2.2.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>jar-no-fork</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### (2). Maven远程发布

```xml
<distributionManagement>
    <snapshotRepository>
        <id>user-snapshot</id>
        <name>user-snapshot</name>
        <url>http://192.168.0.105:8081/nexus/content/repositories/user/</url>
    </snapshotRepository>
</distributionManagement>
```

### (3). Maven配置私服用户名密码

```xml
<server>
    <id>mvc-snapshot</id>
    <username>sharp</username>
    <password>123456</password>
</server>
<server>
    <id>user-snapshot</id>
    <username>sharp</username>
    <password>123456</password>
</server>
```

### (4). Maven聚合管理

```xml
<modules>
    <module>../mvc-core</module>
    <module>../mvc-dao</module>
    <module>../mvc-service</module>
    <module>../mvc-log</module>
</modules>
```

### (5). 配置jetty

```xml
<plugins>
    <plugin>
        <groupId>org.mortbay.jetty</groupId>
        <artifactId>jetty-maven-plugin</artifactId>
    </plugin>
    <!-- <plugin> <groupId>org.codehaus.cargo</groupId> <artifactId>cargo-maven2-plugin</artifactId>   
        <configuration> 配置Tomcat在本地的路径 <container> <containerId>tomcat6x</containerId>   
        <home>D:\Tomcat6.0</home> </container> 配置容器信息 <configuration> statndalone表示独立运行，此时会在特定的目录加载一个相应的web项目，   
        不会加载tomcat中原有的项目 <type>standalone</type> 希望加载路径的目录 <home>D:/webservice/tomcat6</home>   
        properties中可以设置相应的端口的配置 <properties> 端口号设置7676为 <cargo.servlet.port>7676</cargo.servlet.port>   
        </properties> </configuration> </configuration> </plugin> -->

    <!-- <plugin> <groupId>org.codehaus.cargo</groupId> <artifactId>cargo-maven2-plugin</artifactId>   
        基于existing的方式，会把项目发布到系统的Tomcat中的webapps中 <configuration> <container> <containerId>tomcat6x</containerId>   
        <home>D:\Tomcat6.0</home> </container> <configuration> home设置为系统的tomcat目录   
        <type>existing</type> <home>D:\Tomcat6.0</home> </configuration> </configuration>   
        </plugin> -->
</plugins>
```

### (6). 配置tomcat

```xml
<build>
    <finalName>mvc-user</finalName>
    <plugins>
        <plugin>
            <groupId>org.codehaus.cargo</groupId>
            <artifactId>cargo-maven2-plugin</artifactId>
            <configuration>
                <!-- 配置Tomcat在本地的路径 -->
                <container>
                    <containerId>tomcat6x</containerId>
                    <home>D:\tomcat_6.0</home>
                </container>
                <!-- 配置容器信息 -->
                <!--<configuration>  
                     statndalone表示独立运行，此时会在特定的目录加载一个相应的web项目，  
                    不会加载tomcat中原有的项目   
                    <type>standalone</type>  
                     希望加载路径的目录   
                    <home>D:/tomcat_6.0/blank</home>  
                     properties中可以设置相应的端口的配置   
                    <properties>  
                     端口号设置7676为   
                        <cargo.servlet.port>7676</cargo.servlet.port>  
                    </properties>  
                </configuration>  
            --></configuration>
        </plugin>

        <!--<plugin>  
            <groupId>org.codehaus.cargo</groupId>  
            <artifactId>cargo-maven2-plugin</artifactId>  
             基于existing的方式，会把项目发布到系统的Tomcat中的webapps中   
            <configuration>  
                <container>  
                    <containerId>tomcat6x</containerId>  
                    <home>D:\tomcat_6.0</home>  
                </container>  
                <configuration>  
                     home设置为系统的tomcat目录   
                    <type>existing</type>  
                    <home>D:\tomcat_6.0</home>  
                </configuration>  
            </configuration>  
        </plugin>   
    --></plugins>
</build>
```

  [1]: http://pic002.cnblogs.com/images/2012/274814/2012070509215570.gif