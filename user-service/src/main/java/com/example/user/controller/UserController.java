package com.example.user.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.client.FileClient;
import com.example.common.domain.bo.UserBO;
import com.example.common.domain.dto.UserDTO;
import com.example.common.utils.elastic.ElasticUserUtils;
import com.example.user.domain.po.User;
import com.example.user.service.UserService;
import com.example.user.utils.UserMessageQueue;
import com.example.user.utils.UserRedisUtils;
import com.example.user.utils.UserBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.common.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRedisUtils userRedisUtils;
    private final ElasticsearchClient elasticsearchClient;
    private final UserMessageQueue userMessageQueue;
    private final FileClient fileClient;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public Response<Object> getProfile() {
        UserDTO userDTO = ((UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserDTO();
        Map<String, Object> map = new HashMap<>();
        map.put("id", userDTO.getId());
        map.put("uid", userDTO.getUid());
        map.put("username", userDTO.getUsername());
        map.put("nickname", userDTO.getNickname());
        map.put("avatar", userDTO.getAvatar());
        map.put("email", userDTO.getEmail());
        map.put("emailVerified", userDTO.getEmailVerified());
        map.put("registerIp", userDTO.getRegisterIp());
        map.put("createTime", userDTO.getCreateTime());
        map.put("role", userDTO.getRole().getName());
        return Response.success("获取个人信息成功", map);
    }

    @GetMapping("/userProfile")
    public Response<UserDTO> getUserProfile(@RequestParam("username") String username) {
        UserDTO userDTO = userService.getByUsername(username);
        if (userDTO == null) {
            return Response.failed("没有找到该用户");
        }
        userDTO.setPassword(null);
        return Response.success("获取用户信息成功", userDTO);
    }

    @GetMapping("/user/page")
    public Response<Object> getUserPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // 使用 MyBatis-Plus 分页查询
        Page<User> userPage = new Page<>(page, size);
        Page<User> resultPage = userService.page(userPage);

        // 转换为 UserDTO 列表
        List<UserDTO> dtoList = resultPage.getRecords().stream()
                .map(UserBeanUtils::userDTO)
                .collect(Collectors.toList());

        // 构建返回结构
        Map<String, Object> map = new HashMap<>();
        map.put("total", resultPage.getTotal());
        map.put("pages", resultPage.getPages());
        map.put("current", resultPage.getCurrent());
        map.put("size", resultPage.getSize());
        map.put("records", dtoList);

        return Response.success("分页获取用户信息成功", map);
    }

    @GetMapping("/userId")
    public Response<UserDTO> getUserProfileByUid(@RequestParam("uid") String uid) {
        UserDTO userDTO = userService.getByUid(uid);
        if (userDTO == null) {
            return Response.failed("没有找到该用户");
        }
        userDTO.setPassword(null);
        return Response.success("获取用户信息成功", userDTO);
    }

    @GetMapping("/userCount")
    public Response<Long> getTotalUserNumber() {
        return Response.success("获取用户总数成功", userService.count(null));
    }

    @GetMapping("/updateNickname")
    public Response<String> updateNickname(@RequestParam String nickname) {

        UserDTO userDTO = ((UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserDTO();

        if (Objects.equals(userDTO.getNickname(), nickname)) {
            return Response.success("昵称无变化");
        }

        if (!userService.checkNicknameUnique(nickname)) {
            return Response.failed("昵称重复不可用");
        }

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", userDTO.getUid()).set("nickname", nickname);
        boolean updated = userService.update(updateWrapper);
        if (updated) {
            userDTO.setNickname(nickname);
            userRedisUtils.deleteUserCache(userDTO.getUid().toString(), userDTO.getUsername());
//            redisUtils.cacheUser(userDTO);

            userMessageQueue.sendUpdateNickname(UserBeanUtils.userEO(userDTO));
//            try {
//                ElasticUserUtils.updateUserNicknameInEs(elasticsearchClient, UserBeanUtils.userEO(userDTO));
//            } catch (IOException e) {
//                e.printStackTrace();
//                return Response.success("昵称已变更");
//            }
            return Response.success("昵称已变更");
        }

        return Response.failed("发生未知错误");
    }

    @PostMapping("/updatePassword")
    public Response<String> updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword) {

        if (!newPassword.equals(confirmNewPassword)) return Response.failed("两次输入的密码不匹配");

        UserBO userBO = (UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!userService.correctPassword(passwordEncoder, userBO, currentPassword)) {
            return Response.failed("当前密码不匹配");
        }

        // 加密新密码
        String encodeNewPassword = passwordEncoder.encode(newPassword);

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", userBO.getUserDTO().getUid()).set("password", encodeNewPassword);
        boolean updated = userService.update(updateWrapper);
        if (updated) {
            userBO.getUserDTO().setPassword(encodeNewPassword);
            userRedisUtils.deleteUserCache(userBO.getUserDTO().getUid().toString(), userBO.getUserDTO().getUsername());
//            redisUtils.cacheUser(userBO.getUserDTO());
            return Response.success("密码修改成功");
        }
        return Response.failed("发生未知错误");
    }

    @PostMapping("/updateAvatar")
    public Response<Object> updateAvatar(@RequestParam("file") MultipartFile avatar) {
        UserBO userBO = (UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uid = String.valueOf(userBO.getUserDTO().getUid());
        Response<String> stringResponse = fileClient.uploadAvatar(avatar, uid);
        if (!stringResponse.getCode().equals(200)) {
            return Response.failed("设置头像失败");
        }
        String totalAvatarPath = stringResponse.getData();

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).set("avatar", totalAvatarPath);
        boolean updated = userService.update(updateWrapper);
        if (updated) {
            userBO.getUserDTO().setAvatar(totalAvatarPath);
            userRedisUtils.deleteUserCache(userBO.getUserDTO().getUid().toString(), userBO.getUserDTO().getUsername());
//            redisUtils.cacheUser(userBO.getUserDTO());

            userMessageQueue.sendUpdateAvatar(UserBeanUtils.userEO(userBO.getUserDTO()));
//            try {
//                ElasticUserUtils.updateUserAvatarInEs(elasticsearchClient, UserBeanUtils.userEO(userBO.getUserDTO()));
//            } catch (IOException e) {
//                return Response.success("更新头像成功", Map.of("newAvatar", totalAvatarPath));
//            }
            return Response.success("更新头像成功", Map.of("newAvatar", totalAvatarPath));
        }

        return Response.failed("设置头像失败");
    }
}
