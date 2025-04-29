package com.example.security.handle;

import com.example.common.domain.bo.UserBO;
import com.example.common.utils.RedisKeyUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogoutHandler extends SecurityContextLogoutHandler {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        UserBO userBO = (UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedUserKey = RedisKeyUtils.getLoggedUserKey(String.valueOf(userBO.getUserDTO().getUid()));

        redisTemplate.delete(loggedUserKey);
        SecurityContextHolder.clearContext();
        log.info("用户 '{}' 已退出登录", userBO.getUserDTO().getUsername());
    }
}
