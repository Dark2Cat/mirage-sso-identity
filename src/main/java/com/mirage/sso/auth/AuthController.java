package com.mirage.sso.auth;

import com.mirage.sso.common.ApiResponse;
import com.mirage.sso.user.UserService;
import com.mirage.sso.user.UserSummary;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final AuthenticationConfiguration authenticationConfiguration;

    public AuthController(
            AuthService authService,
            UserService userService,
            AuthenticationConfiguration authenticationConfiguration
    ) {
        this.authService = authService;
        this.userService = userService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @PostMapping("/register")
    public ApiResponse<UserSummary> register(@Valid @RequestBody RegisterRequest request) {
        if (!request.password().equals(request.checkPassword())) {
            return ApiResponse.failedData("PASSWORD_NOT_MATCH", "password and checkPassword do not match");
        }
        return ApiResponse.success(userService.register(request.username(), request.password()));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest
    ) throws Exception {
        // 1. 从 Spring Security 配置中获取 AuthenticationManager
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        // 2. 使用用户名和密码进行身份认证，认证失败会抛出 AuthenticationException
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password())
        );

        // 3. 创建新的 SecurityContext 并将认证结果写入其中
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        // 4. 将 SecurityContext 绑定到当前线程，后续请求可直接获取认证信息
        SecurityContextHolder.setContext(context);

        // 5. 将 SecurityContext 持久化到 HTTP Session 中，以便跨请求保持登录状态
        servletRequest.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        // 6. 返回登录响应（包含用户信息及会话凭证）
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(Principal principal, HttpServletResponse response) {
        if (principal == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ApiResponse.failedData("UNAUTHORIZED", "user is not authenticated");
        }
        String username = principal.getName();
        return ApiResponse.success(authService.currentUser(username));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.success();
    }
}
