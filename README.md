# ZookeeperAPI
利用java实现对ZK中Zonde的/创建/修改/删除/读取，以及watch机制的实现

对Znode的增删改查大致分为
1.创建重试策略
2.获取客户端对象
3.开启客户端
4.创建节点/修改几点/查看节点数据/删除节点
5.关闭客户端（加线程休眠保证操作完成）

以及创建watch机制
![学生表](https://github.com/JackFong/MapReduce_1/blob/main/picture/student%E8%A1%A8.png)
