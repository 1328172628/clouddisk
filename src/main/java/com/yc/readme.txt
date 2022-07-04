后端:
1. nginx日志flume采集   hdfs
   程序日志flume采集    hdfs
2. mysql8中的数据库日志
    a. 用户登录
    b. 用户与文件之间的关系. .
    sqoop 采集到 hdfs中.
3.  azkaban来调度以上的任务.
     a. 自定义一个任务，自动生成一些测试数据
     b. 建议在采集任务运行前，先运行这个数据生成任务.
     c. 书写  mr 任务计算一些指标.
          1)  网站的访问量,  客户端统计(  各种浏览器类型  ,  移动端与pc端) ,
          2)  数据文件的流行度.
          3) ....
        存到一个文件  part000,
     d. 写一个任务，读取上面的输出文件，将结果存到数据库中，供后端管理程序.
4. 写一个spring boot后端程序,  将上面计算出来的指标以可视化的方案显示出来.
     百度可视化   chart
5. ganglia监控, azkaban, namenode, resourcemanager 整合到一个界面.
     htm: frameset