package com.example.user.service;

import com.example.common.response.Response;
import com.example.user.domain.po.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AccountService {

    Boolean sendEmailCode(String to, String emailCode);

    Boolean register(User user, HttpServletRequest request);

    Boolean checkUsernameUnique(String username);

    Response<Object> checkEmailUnique(String email);
}
