package com.example.security.handle;

import com.example.common.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class NoLoginHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException {
        res.setContentType("application/json;charset=utf-8");
        String result = "{\"code\": " + ResponseCode.FORBIDDEN.getCode() + ", \"message\": \"未登录或Token无效\", \"data\": null}";
        try {
            res.getWriter().println(result);
        } finally {
            res.getWriter().close();
        }
    }
}
