package com.example.api.client.fallback;

import com.example.api.client.UserClient;
import com.example.common.domain.dto.UserDTO;
import com.example.common.response.Response;
import org.springframework.cloud.openfeign.FallbackFactory;

public class UserClientFallback implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public Response<UserDTO> getUserProfile(String username) {
                return Response.failed("服务不可用" + cause.getMessage(), new UserDTO());
            }

            @Override
            public Response<UserDTO> getUserProfileByUid(String uid) {
                return Response.failed("服务不可用" + cause.getMessage(), new UserDTO());
            }

            @Override
            public Response<Long> getTotalUserNumber() {
                throw new RuntimeException(cause);
            }

            @Override
            public Response<Object> getUserPage(Integer page, Integer size) {
                throw new RuntimeException(cause);
            }
        };
    }
}
