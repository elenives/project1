package com.example.learning.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器 — 在 Controller 处理请求前/后进行拦截
 *
 * 与 Filter 区别：Interceptor 属于 Spring MVC 层，可访问 Handler 信息
 *
 * 演示：通过请求头 X-Token 做简单鉴权（学习用，非生产方案）
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "X-Token";
    private static final String VALID_TOKEN = "learning-token-123";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI();

        if (path.startsWith("/api/public") || path.startsWith("/h2-console")) {
            return true;
        }

        String token = request.getHeader(TOKEN_HEADER);
        if (VALID_TOKEN.equals(token)) {
            log.debug("[Interceptor] 鉴权通过: {}", path);
            return true;
        }

        log.warn("[Interceptor] 鉴权失败: {} (缺少或无效 X-Token)", path);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未授权，请在 Header 中添加 X-Token: learning-token-123\"}");
        return false;
    }
}
