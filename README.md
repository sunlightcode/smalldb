# SmallDB
The open source database SmallDB is a NoSQL database based on the Windows operating system. 

Users can compile and run directly using Microsoft Visual Studio 2010, or learn source code through single-step tracking tools.

SmallDB is the Windows version of the sequoiaDB kernel. It is used for personal research and learning NoSQL database. 

SequoiaDB founder Wang Tao, who worked at IBM, is responsible for the development of DB2 database, and is one of the best database engine development experts in the industry.

开源数据库 SmallDB 是一款基于 Windows 操作系统的 NoSQL 数据库。

用户可以使用微软 Visual Studio 2010 直接编译运行，也可以通过单步跟踪工具学习源代码。

SmallDB是sequoiaDB巨杉数据库内核的Windows版本，用于个人研究学习NoSQL数据库使用。

巨杉数据库创始人王涛，曾经就职于在IBM，负责DB2数据库的研发，是国内最优秀数据库专家之一。

## Download Boost

Please download the Boost files first and put it in the project directory, then compile it.

> Download link: [https://pan.baidu.com/s/1idwawd-teLMC79DzG2YXjg]()

请先下载Boost文件放入工程目录，再进行编译。

> 下载链接：[https://pan.baidu.com/s/1idwawd-teLMC79DzG2YXjg]()

## The SmallDB command line
1.Connect to a local or remote server

```
smalldb> connect localhost 85817
```

2.Insert data rows

```
smalldb> insert {"_id":"8","name":"smalldb"}
```

3.Query data

```
smalldb> query {"_id":"8"}
```

## SmallDB 命令行操作
1.连接本地或远程服务器

```
smalldb> connect localhost 85817
```

2.插入数据行

```
smalldb> insert {"_id":"8","name":"smalldb"}
```


3.查询数据

```
smalldb> query {"_id":"8"}
```




