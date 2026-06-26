# Spring Boot 学习项目

涵盖 **Spring Boot MVC / DI / 事务 / AOP / 拦截器**、**数据库 SQL 与索引**、**Redis 数据结构与分布式锁**、**RocketMQ 生产者与消费者**。

## 环境要求

- JDK 17+
- Maven 3.8+
- Docker（可选，用于 MySQL / Redis / RocketMQ）

## 快速启动

### 方式一：零依赖（H2 内存库）

```bash
mvn spring-boot:run
```

可体验：MVC、DI、事务、AOP、拦截器、H2 控制台。

- 健康检查：http://localhost:8080/api/public/health
- H2 控制台：http://localhost:8080/h2-console（JDBC URL: `jdbc:h2:mem:learning_db`）

### 方式二：完整环境（Docker，推荐）

**前置条件**：Docker Desktop 已启动（`docker ps` 不报错）。若拉镜像很慢或失败，请在 Docker Desktop → Settings → Docker Engine 配置镜像加速（**只用英文引号和逗号**）：

```json
{
  "experimental": false,
  "registry-mirrors": [
    "https://docker.m.daocloud.io"
  ]
}
```

若出现 `429 Too Many Requests`，说明某个镜像站被限流，请**删掉** `docker.xuanyuan.me` 等失效源，只保留一个 mirror，或运行项目里的辅助脚本：

```powershell
.\docker-pull.ps1
docker compose up -d --build
```

**一键启动全部服务（MySQL + Redis + RocketMQ + 应用）：**

```bash
docker compose up -d --build
```

等待应用启动后（约 2～3 分钟），运行演示脚本：

```powershell
.\demo-docker.ps1
```

或手动验证：

```bash
curl http://localhost:8080/api/public/health
```

查看应用日志：

```bash
docker compose logs -f app
```

停止所有服务：

```bash
docker compose down
```

> **说明**：MySQL 映射到宿主机 `3307` 端口（避免与本机 3306 冲突）。容器内应用通过服务名 `mysql`、`redis`、`rocketmq-namesrv` 通信。

### 方式三：Docker 中间件 + 本地运行应用

```bash
docker compose up -d mysql redis rocketmq-namesrv rocketmq-broker
mvn spring-boot:run -Dspring-boot.run.profiles=mysql,full
```

## 鉴权说明

除 `/api/public/**` 外，所有 `/api/**` 接口需在 Header 添加：

```
X-Token: learning-token-123
```

## 知识点与 API 对照

| 知识点 | 代码位置 | 测试接口 |
|--------|----------|----------|
| **MVC** | `controller/*` | `GET /api/users` |
| **依赖注入** | `@RequiredArgsConstructor` | 各 Service 构造器注入 |
| **事务** | `OrderService.createOrder` | `POST /api/orders` |
| **事务回滚** | `OrderService.demoRollback` | `POST /api/orders/demo-rollback?userId=1&amount=1` |
| **AOP** | `aspect/LogAspect` | 调用任意 Service，看日志 `[AOP]` |
| **拦截器** | `interceptor/AuthInterceptor` | 不带 Token 访问 `/api/users` 返回 401 |
| **SQL / 索引** | `schema.sql` | H2/MySQL 中查看 `idx_user_email` 等索引 |
| **Redis 数据结构** | `redis/RedisDemoService` | `POST /api/redis/string?key=a&value=b` |
| **分布式锁** | `redis/RedisDistributedLock` | `POST /api/redis/lock-demo` |
| **RocketMQ 生产** | `mq/MessageProducer` | `POST /api/mq/send?message=hello` |
| **RocketMQ 消费** | `mq/MessageConsumer` | 启用 `full` profile 后看日志 |

## 示例请求

```bash
# 公开接口
curl http://localhost:8080/api/public/topics

# 查询用户（需 Token）
curl -H "X-Token: learning-token-123" http://localhost:8080/api/users

# 创建订单（事务：扣余额 + 写订单）
curl -X POST -H "Content-Type: application/json" \
  -H "X-Token: learning-token-123" \
  -d "{\"userId\":1,\"product\":\"键盘\",\"amount\":199}" \
  http://localhost:8080/api/orders

# Redis String
curl -X POST -H "X-Token: learning-token-123" \
  "http://localhost:8080/api/redis/string?key=demo&value=hello"

# RocketMQ 发消息（需 RocketMQ 运行）
curl -X POST -H "X-Token: learning-token-123" \
  "http://localhost:8080/api/mq/send?tag=notify&message=测试消息"
```

## 项目结构

```
src/main/java/com/example/learning/
├── aspect/          # AOP 切面
├── config/          # Web、Redis 配置
├── controller/      # MVC 控制器
├── dto/             # 数据传输对象
├── entity/          # JPA 实体
├── interceptor/     # 拦截器
├── mq/              # RocketMQ 生产者/消费者
├── redis/           # Redis 数据结构 & 分布式锁
├── repository/      # 数据访问层
└── service/         # 业务层（事务、DI）
```

## 核心概念速查

### 数据库事务（ACID）

- **原子性**：下单 + 扣款要么全成功，要么全回滚
- **一致性**：余额不为负等业务约束
- **隔离性**：并发事务互不干扰（Spring 默认 READ_COMMITTED）
- **持久性**：提交后数据落盘

### Redis 数据结构

- **String**：缓存、计数器
- **Hash**：对象字段
- **List**：队列
- **Set**：去重
- **ZSet**：排行榜

### RocketMQ

- **NameServer**：路由中心
- **Topic / Tag**：主题与子分类
- **Producer Group / Consumer Group**：生产/消费组
