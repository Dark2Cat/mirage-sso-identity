package com.mirage.sso.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 安全配置类
 *
 * 职责：
 * 1. 定义 OAuth2 授权服务器的安全过滤链（@Order(1)）
 * 2. 定义 API 接口的安全过滤链（@Order(2)）
 * 3. 注册各类安全相关 Bean：密码编码器、CORS、JWT 签名密钥、授权服务器设置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * OAuth2 授权服务器安全过滤链（优先级最高）
     *
     * 处理 /oauth2/*、/.well-known/openid-configuration 等 OAuth2/OpenID Connect 协议端点。
     * 当请求匹配此过滤链时，不会继续走到 @Order(2) 的 apiSecurityFilterChain。
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // 应用 OAuth2 授权服务器的默认安全配置（包括协议端点映射、认证方式等）
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // 启用 OpenID Connect 1.0 支持（提供 /.well-known/openid-configuration 端点）
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        // 异常处理：未认证时重定向到 /login 页面（用于授权码流程中用户未登录的情况）
        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

        return http.cors(Customizer.withDefaults()).build();
    }

    /**
     * API 接口安全过滤链（优先级第二）
     *
     * 处理 /api/*、/actuator/*、/swagger-ui/* 等非 OAuth2 协议端点的 HTTP 请求。
     * 这是系统主要的 API 安全层，负责登录、注册、管理后台等接口的访问控制。
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // ========== CORS 跨域配置 ==========
                // 允许前端跨域请求 API，具体允许的来源由 application.yml 中的 app.cors.allowed-origins 控制
                .cors(Customizer.withDefaults())

                // ========== CSRF 防护配置 ==========
                // 对 /api/** 路径关闭 CSRF 防护，因为 API 请求通常使用 Token/Header 验证而非 Cookie Session
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                // ========== URL 级别鉴权规则 ==========
                .authorizeHttpRequests(authorize -> authorize
                        // ---- 认证免放行接口（permitAll）----
                        // 登录接口：允许未登录用户访问
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
                        // 门户应用列表：允许未登录用户查看可用的 SSO 应用
                        .requestMatchers("/api/portal/apps").permitAll()
                        // 健康检查 & 接口文档：允许未登录用户访问
                        .requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // ---- 其他所有请求 ----
                        // 除以上放行地址外，所有请求需要用户已通过认证
                        .anyRequest().authenticated()
                )

                // ========== 认证方式 ==========
                // 启用表单登录（适用于 SSO 授权码流程中用户被重定向到登录页的场景）
                .formLogin(Customizer.withDefaults())
                // 启用 HTTP Basic 认证（适用于一些简单的后台工具或 curl 测试）
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * JWT 签名密钥源
     *
     * 生成一个 RSA-2048 密钥对，以 JWK (JSON Web Key) 格式暴露。
     * OAuth2 授权服务器使用此密钥对签发 access_token 和 id_token 的 JWT 签名。
     * 每次应用启动都会生成新的密钥对（重启后之前签发的 token 会失效）。
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 构建 RSA 公钥 + 私钥的 JWK，分配随机 key ID
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    /**
     * OAuth2 授权服务器设置
     *
     * 配置授权服务器的 issuer（签发者）URL。
     * issuer 取自 application.yml 中的 app.security.issuer 配置项。
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings(AppProperties properties) {
        return AuthorizationServerSettings.builder()
                .issuer(properties.security().issuer())
                .build();
    }

    /**
     * 密码编码器
     *
     * 使用 Spring Security 推荐的委托式密码编码器。
     * 默认使用 BCrypt 算法对密码进行哈希，密码字符串以 {bcrypt} 前缀标识算法类型。
     * 开发测试环境下也支持 {noop} 前缀的明文密码，便于调试。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * CORS 跨域配置源
     *
     * 从 application.yml 的 app.cors.allowed-origins 读取允许的跨域来源列表。
     * 支持所有请求头和方法，允许携带凭证（Cookie/Authorization header）。
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(AppProperties properties) {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许跨域的来源地址列表（由配置文件控制）
        configuration.setAllowedOrigins(properties.cors().allowedOrigins());
        // 允许所有请求头
        configuration.addAllowedHeader("*");
        // 允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）
        configuration.addAllowedMethod("*");
        // 允许跨域请求携带凭证（Cookie、Authorization 头等）
        configuration.setAllowCredentials(true);

        // 将配置应用到所有路径（/**）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 生成 RSA-2048 密钥对
     *
     * 内部工具方法，由 jwkSource() 调用。
     * 使用标准 Java KeyPairGenerator API，算法为 RSA，密钥长度 2048 位。
     */
    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception exception) {
            throw new IllegalStateException("RSA 密钥对生成失败", exception);
        }
    }
}