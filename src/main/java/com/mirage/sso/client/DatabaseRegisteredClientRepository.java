package com.mirage.sso.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.Duration;
import java.util.List;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

/**
 * 基于数据库的 OAuth2 RegisteredClient 存储库实现
 *
 * 将 sso_client 表中的客户端配置转换为 Spring Security OAuth2 运行时的 RegisteredClient 对象。
 * 通过 @Component 自动注册，无需在 SecurityConfig 中显式声明 @Bean。
 *
 * OAuth2 授权服务器的 RegisteredClient 职责：
 * - 验证 client_id / client_secret
 * - 验证回调 URI 是否在白名单内
 * - 验证 authorization grant type 是否被允许
 * - 配置 PKCE / consent 要求
 * - 提供 token 有效期设置（access_token、refresh_token）
 */
@Component
public class DatabaseRegisteredClientRepository implements RegisteredClientRepository {
    private final ClientMapper clientMapper;

    public DatabaseRegisteredClientRepository(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    /**
     * 保存 RegisteredClient（不支持）
     *
     * 客户端注册和更新通过管理后台 API（ClientAdminController）直接操作 sso_client 表完成，
     * 不走 OAuth2 协议的动态注册端点，因此此处直接抛出 UnsupportedOperationException。
     */
    @Override
    public void save(RegisteredClient registeredClient) {
        throw new UnsupportedOperationException("RegisteredClient 的写入操作由客户端管理 API 处理");
    }

    /**
     * 根据主键 ID 查找客户端
     *
     * @param id 客户端主键（字符串形式，实际为数据库自增 Long ID）
     * @return RegisteredClient 对象，未找到或已禁用时返回 null
     */
    @Override
    public RegisteredClient findById(String id) {
        return findEntity(new LambdaQueryWrapper<ClientEntity>().eq(ClientEntity::getId, id));
    }

    /**
     * 根据 client_id 查找客户端
     *
     * 这是 OAuth2 授权码流程中最常用的查询方法。
     * 授权服务器在接收到授权请求后，会通过此方法验证 client_id 的合法性。
     *
     * @param clientId OAuth2 客户端标识符
     * @return RegisteredClient 对象，未找到或已禁用时返回 null
     */
    @Override
    public RegisteredClient findByClientId(String clientId) {
        return findEntity(new LambdaQueryWrapper<ClientEntity>().eq(ClientEntity::getClientId, clientId));
    }

    /**
     * 核心方法：将数据库实体 ClientEntity 转换为 Spring Security OAuth2 的 RegisteredClient
     *
     * 转换逻辑：
     * 1. 查询 sso_client 表获取 ClientEntity
     * 2. 检查客户端状态是否为 ENABLED，非启用状态返回 null（不可用）
     * 3. 从 ClientEntity 各字段映射为 RegisteredClient 的属性
     *    - 基础信息：id、clientId、clientName
     *    - 客户端设置：PKCE 要求、consent 要求
     *    - Token 设置：access_token / refresh_token 有效期
     *    - 认证方式：有 secret 时支持 BASIC/POST，无 secret 时使用 NONE（公开客户端）
     *    - 回调 URI、登出回调 URI、授权类型、作用域
     *
     * @param wrapper MyBatis-Plus 查询条件包装器（按 id 或按 clientId 查询）
     * @return RegisteredClient 对象，客户端不存在或已禁用时返回 null
     */
    private RegisteredClient findEntity(LambdaQueryWrapper<ClientEntity> wrapper) {
        // 1. 从数据库查询客户端实体
        ClientEntity entity = clientMapper.selectOne(wrapper.last("limit 1"));

        // 2. 客户端不存在或已被禁用 → 返回 null（OAuth2 协议层会拒绝请求）
        if (entity == null || entity.getStatus() != ClientStatus.ENABLED) {
            return null;
        }

        // 3. 构建 RegisteredClient
        RegisteredClient.Builder builder = RegisteredClient.withId(String.valueOf(entity.getId()))
                .clientId(entity.getClientId())
                .clientName(entity.getClientName())

                // ---------- 客户端设置 ----------
                .clientSettings(ClientSettings.builder()
                        // SPA（单页应用）和 MOBILE（移动端）强制要求 PKCE（Proof Key for Code Exchange）
                        // PKCE 可以在没有 client_secret 的情况下防止授权码拦截攻击
                        .requireProofKey(
                                entity.getClientType() == ClientType.SPA ||
                                entity.getClientType() == ClientType.MOBILE
                        )
                        // 是否需要用户手动确认授权（consent 页面）
                        .requireAuthorizationConsent(Boolean.TRUE.equals(entity.getRequireConsent()))
                        .build())

                // ---------- Token 设置 ----------
                .tokenSettings(TokenSettings.builder()
                        // access_token 有效期（秒），由数据库字段控制
                        .accessTokenTimeToLive(Duration.ofSeconds(entity.getAccessTokenTtl()))
                        // refresh_token 有效期（秒），由数据库字段控制
                        .refreshTokenTimeToLive(Duration.ofSeconds(entity.getRefreshTokenTtl()))
                        // 每次刷新 Token 时颁发新的 refresh_token，旧的失效（增强安全性）
                        .reuseRefreshTokens(false)
                        .build());

        // 4. 配置客户端认证方式
        //    无 client_secret → 公开客户端（如纯前端 SPA，使用 PKCE 流程）
        //    有 client_secret → 机密客户端，支持 BASIC 和 POST 两种方式传递 secret
        if (entity.getClientSecretHash() == null || entity.getClientSecretHash().isBlank()) {
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.NONE);
        } else {
            builder.clientSecret(entity.getClientSecretHash())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)  // HTTP Basic Auth 头传递
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);  // POST 表单体传递
        }

        // 5. 从 JSON 数组字符串解析并设置多值字段
        //    数据库中这些字段以 JSON 数组字符串存储（如 ["value1","value2"]），
        //    也兼容逗号分隔的简单格式（value1,value2）
        jsonValues(entity.getRedirectUris()).forEach(builder::redirectUri);              // 授权成功后的回调 URI 白名单
        jsonValues(entity.getPostLogoutRedirectUris()).forEach(builder::postLogoutRedirectUri); // 登出后的回调 URI 白名单
        jsonValues(entity.getGrantTypes()).stream()
                .map(AuthorizationGrantType::new)
                .forEach(builder::authorizationGrantType);                               // 允许的授权类型（authorization_code、refresh_token 等）
        jsonValues(entity.getScopes()).forEach(builder::scope);                          // 允许的 OAuth2 作用域

        return builder.build();
    }

    /**
     * 解析 JSON 数组字符串为字符串列表
     *
     * 数据库中以字符串形式存储多值字段，支持两种格式：
     * 1. JSON 数组格式：["val1","val2"]（标准格式）
     * 2. 逗号分隔格式：val1,val2（兼容简单格式）
     *
     * @param jsonArray 数据库中的原始字符串
     * @return 解析后的字符串列表，输入为 null/空/空白时返回空列表
     */
    private static List<String> jsonValues(String jsonArray) {
        // null 或空白字符串 → 返回空列表
        if (jsonArray == null || jsonArray.isBlank()) {
            return List.of();
        }

        String trimmed = jsonArray.trim();

        // 非 JSON 数组格式（不以 [ 开头或以 ] 结尾）→ 按逗号分隔的简单格式处理
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            return List.of(trimmed.split(",")).stream()
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .toList();
        }

        // JSON 数组格式：去掉 [ 和 ]，按逗号分隔并去掉引号
        String content = trimmed.substring(1, trimmed.length() - 1).trim();
        if (content.isBlank()) {
            return List.of();
        }
        return List.of(content.split(",")).stream()
                .map(String::trim)
                .map(value -> value.replaceAll("^\"|\"$", ""))  // 去除每个值的引号
                .filter(value -> !value.isBlank())
                .toList();
    }
}