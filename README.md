# Mirage SSO Identity

Mirage SSO 单点登录认证中心后端项目。

## 技术基线

- Java 21
- Spring Boot 3.3.13
- Spring Security 6.3.x
- Spring Authorization Server 1.3.x
- Gradle 8.8 Wrapper
- MySQL 8.0
- Redis 7.2
- MyBatis Plus 3.5.7
- Flyway

## MySQL 模式

```bash
docker compose -f deploy/docker-compose.yml up -d
./gradlew bootRun
```

默认地址：

```text
http://localhost:8080
```

## 内存联调模式

不依赖 Docker、MySQL、Redis，使用 H2 内存库和种子数据：

```bash
./gradlew bootRun --args='--spring.profiles.active=memory --server.port=18082 --app.security.issuer=http://127.0.0.1:18082'
```

内存联调地址：

```text
http://localhost:18082
```

默认账号：

```text
admin / mirage@2026
```

健康检查：

```text
http://localhost:18082/actuator/health
```

OpenAPI：

```text
http://localhost:18082/swagger-ui/index.html
```
