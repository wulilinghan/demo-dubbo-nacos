# springboot-dubbo-nacos-demo
基于springboot搭建dubbo，使用nacos作为注册中心, shiro_redis做权限.
## 项目结构

sdnd  
├── admin_docs -- 文档,SQL文件等  
├── sdnd-api -- 接口定义层  
├── sdnd-common -- 工具类及通用代码  
├── sdnd-common-mapper -- 数据持久层  
├── sdnd-provide -- 8031 生产者  
├── sdnd-web -- 8032 消费者  
├── sdnd-web-test -- 测试模块  
└── xxl-job-admin -- 8080 XXL-JOB分布式任务调度平台 2.3.0版本  

-------------------------------------------------------------
1. 统一返回对象
2. 全局异常处理
3. aop日志记录
4. 集成Redis
5. 集成Rabbitmq
- [ ] 消息可靠性投递
- [ ] 处理消息重复消费
6. 邮件发送
7. 使用logback记录日志,控制台彩色打印
8. 集成分布式任务调度平台XXL-JOB 2.3.0版本
9. 添加分布式锁[Redission](https://github.com/redisson/redisson )(service-provide模块测试类RedissionTest)
10. 添加接口限流功能(@AccessLimit注解)
11. 使用screw导出数据库文档(web-test模块下测试类ScrewApplicationTests)
12. [数据+模板生成PDF](https://github.com/tanglinghan/pdf-demo)
13. - [ ] CompletableFuture异步编程


本地swagger文档地址：http://localhost:8032/doc.html



