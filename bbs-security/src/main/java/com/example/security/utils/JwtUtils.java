package com.example.security.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.security.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@ConditionalOnProperty("jwt")
public class JwtUtils {
    private final JwtProperties jwtProperties;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * accessToken是对已认证用户颁发的凭证，后续请求只要携带上accessToken并解析通过，则对请求放行操作
     * @param uid 用户ID
     * @return accessToken（访问凭证）
     */
    public String createAccessToken(String uid) {
        SecretKey sign = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(IdUtil.simpleUUID())
                .setSubject(uid)
                .setAudience("access") //标记这是个accessToken
                .signWith(sign, SignatureAlgorithm.HS256)
                .setExpiration(DateUtil.date(System.currentTimeMillis() + jwtProperties.getAccessTokenTTL()))
                .compressWith(CompressionCodecs.GZIP);

        return jwtBuilder.compact();
    }

    /**
     * 由于accessToken过期时间不宜太长，所以会遇到正在进行业务操作时凭证过期的情况。
     * 因此需要一个refreshToken对访问凭证进行无感知刷新的操作，防止降低用户体验。
     * @param uid 用户ID
     * @return refreshToken（刷新访问凭证的凭证）
     */
    public String createRefreshToken(String uid) {
        SecretKey sign = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(IdUtil.simpleUUID())
                .setSubject(uid)
                .setAudience("refresh") //标记这是个refreshToken
                .signWith(sign, SignatureAlgorithm.HS256)
                .setExpiration(DateUtil.date(System.currentTimeMillis() + jwtProperties.getRefreshTokenTTL()))
                .compressWith(CompressionCodecs.GZIP);

        return jwtBuilder.compact();
    }

    public Claims parseTokenToClaims(String token) {
        SecretKey sign = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        if (isTokenValid(token)) {
            return Jwts.parserBuilder().setSigningKey(sign).build().parseClaimsJws(token).getBody();
        }
        return null;
    }

    public boolean isTokenValid(String token) {
        SecretKey sign = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        try {
            Jwts.parserBuilder().setSigningKey(sign).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            switch (e.getClaims().getAudience()) {
                case "access" -> log.info("用户 '{}' 的 accessToken 已过期", e.getClaims().getSubject());
                case "refresh" -> {
                    log.info("用户 '{}' 的 refreshToken 已过期，已退出登录", e.getClaims().getSubject());
                }
            }
            return false;
        } catch (Exception e) {
            log.error("解析凭证出错: {}", e.getMessage());
            throw new JwtException(e.getMessage());
        }
    }

    public Claims getExpiredTokenClaims(String token) {
        SecretKey sign = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        try {
            return Jwts.parserBuilder().setSigningKey(sign).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (Exception e) {
            log.error("错误的凭证: {}", e.getMessage());
            throw new JwtException(e.getMessage());
        }
    }
}
