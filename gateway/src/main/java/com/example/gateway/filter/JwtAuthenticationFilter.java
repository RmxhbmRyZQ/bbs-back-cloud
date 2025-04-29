package com.example.gateway.filter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.response.ResponseCode;
import com.example.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Token校验过滤器，在每次前端请求前，检查是否携带Token。
 * 没有携带就放行去执行后续方法，携带了就解析出当前访问者的所有信息，然后存入SecurityContext。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.token-header}")
    private String tokenHeader;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    @Value("${jwt.auto-refresh-ttl}")
    private Long autoRefreshTTL;

    private final JwtUtils jwtUtils;

    private Mono<Void> expireTokenRender(ServerHttpResponse response) {
        return render(ResponseCode.UNAUTHORIZED.getCode(), "访问凭证已过期", response);
    }

    private Mono<Void> loginAgainRender(ServerHttpResponse response) {
        return render(ResponseCode.AUTHENTICATION_EXPIRED.getCode(), "请重新登录", response);
    }

    private Mono<Void> invalidTokenRender(ServerHttpResponse response) {
        return render(ResponseCode.PRECONDITION_FAILED.getCode(), "无效凭证", response);
    }

    private Mono<Void> loggedOutRender(ServerHttpResponse response) {
        return render(ResponseCode.SUCCESS.getCode(), "已退出登录", response);
    }

    private Mono<Void> render(int code, String msg, ServerHttpResponse response) {
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        String result = "{\"code\": " + code + ", \"message\": " + msg + "}";
        byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String tokenFromRequestHeader = exchange.getRequest().getHeaders().getFirst(tokenHeader);
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if (StrUtil.isBlank(tokenFromRequestHeader) || !tokenFromRequestHeader.startsWith(tokenPrefix)) {
//            filterChain.doFilter(request, response); /* 请求头中未携带token或token格式有误的话, 放行 */

            ServerHttpRequest newRequest = request.mutate()
                    .header("User-ID", "")
                    .build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
        // 请求头中解析得到的 token 可能的类型有两种：accessToken 和 refreshToken，前者用于日常请求，后者用于刷新前者，实现不中断的操作体验
        String token = tokenFromRequestHeader.replace(tokenPrefix, "");
        String uid;

        boolean isTokenValid;
        try {
            isTokenValid = jwtUtils.isTokenValid(token);
        } catch (Exception e) {
            return invalidTokenRender(exchange.getResponse());
        }

        String requestURI = request.getURI().getPath();
        if (isTokenValid) {
            Claims claims = jwtUtils.parseTokenToClaims(token);
            String tokenType = claims.getAudience();
            // 如果我一直使用access token进行访问，那么会一直刷新他的过期时间，但是不会刷新refresh token的过期时间，导致access过期了就马上过期
            if (ObjectUtil.notEqual("/refresh", requestURI)) { // 非刷新凭证的操作
                switch (tokenType) {
                    case "refresh" -> {
                        uid = null;
                        log.warn("用户 '{}' 携带的凭证与请求类型不匹配", claims.getSubject());
                    }
                    case "access" -> {
                        uid = claims.getSubject();
                        if (ObjectUtil.notEqual("/logout", requestURI) & (claims.getExpiration().getTime() - DateUtil.date().getTime()) <= autoRefreshTTL) {
                            String accessToken = jwtUtils.createAccessToken(uid);
                            response.getHeaders().add(tokenHeader, accessToken);
                            log.info("用户 '{}' 的 accessToken 临近过期且仍然活跃，已重新颁发", uid);
                        }
                    }
                    default -> {
                        uid = null;
                        log.warn("用户 '{}' 携带的凭证的类型有误", uid);
                    }
                }
            }  else { // 刷新凭证的操作
                switch (tokenType) {
                    case "access" -> {
                        uid = null;
                        log.warn("用户 '{}' 携带的凭证与请求类型不匹配", uid);
                    }
                    case "refresh" -> uid = claims.getSubject();
                    default -> {
                        uid = null;
                        log.warn("用户 '{}' 携带的凭证的类型有误", uid);
                    }
                }
            }
        } else {
            if (ObjectUtil.equal("/logout", requestURI)) {
                log.info("用户 '{}' 已退出登录", jwtUtils.getExpiredTokenClaims(token).getSubject());
                return loggedOutRender(response); // 如果 accessToken 过期，且访问的是 "/logout"，则返回已退出的响应
            } else if (ObjectUtil.equal("/refresh", requestURI)) {
                return loginAgainRender(response); // refreshToken 过期则需要重新登录（JwtUtil中已打印此日志，此处省略过期日志的打印）
            } else {
                return expireTokenRender(response); // accessToken 过期则返回过期信息，前端再通过 refreshToken 尝试获取新的 accessToken
            }
        }

        if (ObjectUtil.isNull(uid)) {
            log.info("用户 '{}' 的登录缓存丢失，已退出登录", uid);
            return loginAgainRender(response);
        }

        ServerHttpRequest newRequest = request.mutate()
                .header("User-ID", uid)
                .build();
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
