# 介绍

基于 [Flip](https://github.com/anyshift/flip) 实现的一个分布式的后端

技术栈：Spring Cloud组件如 nacos、openfeign、gateway，rabbitmq，redis，mybatis-plus，elasticsearch，sentinel，docker

**论坛系统开发 | Spring Cloud + MyBatis-Plus + Vue 3 + Docker**

- **项目功能**：实现用户注册与登录、帖子浏览与搜索、评论与回复、敏感词过滤等核心功能。
- **系统架构**：采用前后端分离设计，后端基于微服务架构划分为用户服务、文件服务、帖子服务、评论服务、监控服务等模块。
- **高并发优化**：通过 RabbitMQ 将 Elasticsearch 相关操作异步处理，提升系统响应速度与用户体验。
- **数据设计**：设计用户表、帖子表、评论表、敏感词表、标签表，结合 MyBatis-Plus 分页插件优化数据查询性能。
- **安全与风控**：集成 Spring Security 与网关实现权限校验；基于 Sentinel 对 OpenFeign 接口进行熔断和降级处理，并使用 Nacos 持久化规则配置。
- **网关与路由**：使用 Spring Cloud Gateway 实现请求路由、负载均衡与 JWT 鉴权。
- **配置管理**：借助 Nacos 实现统一配置中心，抽离各微服务共性配置，提升系统可维护性。
- **搜索功能**：集成 Elasticsearch 实现帖子与用户信息的快速全文检索。

# 使用

先使用compose下的docker-compose.yml进行部署，将compose文件夹复制到Ubuntu中，并使用下面命令来部署

```shell
sudo docker compose up -d
```

运行bbs-init中的测试方法，上传配置文件到nacos中

接着就可以运行里面的微服务了，包括：

- user-service
- post-service
- search-service
- monitor-service
- file-service
- comment-service