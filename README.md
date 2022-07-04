# clouddisk
云盘项目 基于hadoop集群

搭建hadoop集群
![image](https://user-images.githubusercontent.com/56126256/177105813-abb6b4d3-5665-405c-8be8-1c2996d9f8d5.png)

使用flume 进行日志采集  
          采集nginx日志 创建java job，分析用户来源，各个服务使用情况，从而为各个服务分配资源，拆分服务
使用sqoop 采集clouddisk 数据库数据
          
          azkaban 进行任务调度

node1 节点 搭建nginx 实现云盘项目动静分离，服务负载均衡
           搭建redis 实现用户状态保持，布隆过滤器实现云盘文件去重（采用md5算法）
