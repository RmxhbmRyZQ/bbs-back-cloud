package com.example.security.filter;

import cn.hutool.core.util.StrUtil;
import com.example.api.client.UserClient;
import com.example.common.domain.bo.UserBO;
import com.example.common.domain.dto.UserDTO;
import com.example.common.utils.RedisKeyUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class UserHeaderAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private UserClient userClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader("User-ID");
        if (StrUtil.isBlank(userId)) {
            filterChain.doFilter(request, response);
            return;
        }

        String redisKey = RedisKeyUtils.getKeyUserUidKey(userId);
        Object o = redisTemplate.opsForValue().get(redisKey);

        if (o == null) {
            o = userClient.getUserProfileByUid(userId).getData();
        }

        if (o != null && o instanceof UserDTO) {
            UserBO userBO = new UserBO((UserDTO) o);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userBO, null, userBO.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
