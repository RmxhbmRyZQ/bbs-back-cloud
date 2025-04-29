package com.example.security.handle;

import com.example.common.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class NoPermissionHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException e) throws IOException {
        res.setContentType("application/json;charset=utf-8");
        String result = "{\"code\": " + ResponseCode.FORBIDDEN.getCode() + ", \"message\": \"权限不足，拒绝访问\"}";
        try {
            res.getWriter().println(result);
        } finally {
            res.getWriter().close();
        }
    }
}
