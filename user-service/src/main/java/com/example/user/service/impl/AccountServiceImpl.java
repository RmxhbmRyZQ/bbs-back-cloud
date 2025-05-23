package com.example.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.api.client.FileClient;
import com.example.common.domain.dto.UserDTO;
import com.example.common.domain.eo.UserEO;
import com.example.common.response.Response;
import com.example.common.utils.IpUtils;
import com.example.common.utils.RedisKeyUtils;
import com.example.common.utils.elastic.ElasticUserUtils;
import com.example.user.domain.po.Role;
import com.example.user.domain.po.User;
import com.example.user.mapper.UserMapper;
import com.example.user.service.AccountService;
import com.example.user.service.UserService;
import com.example.user.utils.UserBeanUtils;
import com.example.user.utils.UserMessageQueue;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Value("${mail.from}")
    private String from;

    @Value("${mail.register.subject}")
    private String subject;

    @Resource
    private JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserService userService;

    private final UserMapper userMapper;

    private final ElasticsearchClient elasticsearchClient;

    private final UserMessageQueue userMessageQueue;

    private final PasswordEncoder passwordEncoder;

    private final FileClient fileClient;

    /**
     * 用户注册
     *
     * @param user           用户实体类
     * @param request        请求对象
     * @return 响应体
     */
    @Override
    @Transactional
    public Boolean register(User user, HttpServletRequest request) {

        /* 设置用户的注册IP */
        String ip = IpUtils.getIP(request);
        user.setRegisterIp(ip);

        /* 通过 BcryptPasswordEncode 加密算法加密前端传过来的用户密码 */
        String encodePassword = passwordEncoder.encode(user.getPassword());
        /* 原始密码替换为加密后的密码 */
        user.setPassword(encodePassword);

        /* 设置用户的盐值 */
        user.setSalt(IdUtil.simpleUUID().substring(0, 5));

        /* 设置用户的角色 */
        Role role = new Role();
        Long totalUsers = userMapper.selectCount(null);
        if (ObjectUtil.isNull(totalUsers)) {
            role.setRid(1); //管理员
            user.setEmailVerified(true);
        } else {
            role.setRid(4); //未认证成员
            user.setEmailVerified(false);
        }
        user.setRole(role);

        user.setNickname(user.getUsername());

        /* 用户数据插入到数据库 */
        int insertUserResult = userMapper.insert(user);

        /* 用户角色插入到数据库 */
        int insertUserRoleResult = userMapper.insertUserRole(user.getUid(), user.getRole().getRid());

        /* 用户数据和角色数据插入数据库成功 */
        if (insertUserResult > 0 && insertUserRoleResult > 0) {
            Role realRole = userService.loadUserRoleByUid(String.valueOf(user.getUid()));
            user.setRole(realRole);

            String random = DigestUtil.md5Hex(user.getEmail() + user.getSalt());
            String avatar = fileClient.initAvatar(random, user.getUid()).getData();
//            String avatar = AvatarUtils.createAvatar(avatarPath, avatarPrefix, avatarRemotePrefix, avatarRemoteSuffix, random, user.getUid());
            user.setAvatar(avatar);
            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            userUpdateWrapper.eq("uid", user.getUid()).set("avatar", avatar);
            userMapper.update(null, userUpdateWrapper);

            /* 变更Redis中的用户数 */
            String userNumberKey = RedisKeyUtils.getUserNumberKey();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(userNumberKey))) {
                redisTemplate.opsForValue().increment(userNumberKey);
            } else redisTemplate.opsForValue().set(userNumberKey, userMapper.selectCount(null));

            userMessageQueue.sendInsert(UserBeanUtils.userEO(user));
//            try {
//                ElasticUserUtils.insertUserToEs(elasticsearchClient, UserBeanUtils.userEO(user));
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
////                return true;
//            }
            return true;
        }

        return false;
    }

    /**
     * 校验用户名唯一性
     *
     * @param username 用户名
     * @return result
     */
    @Override
    public Boolean checkUsernameUnique(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return ObjectUtil.isNull(userService.getOne(queryWrapper));
    }

    /**
     * 校验邮箱唯一性
     *
     * @param email 邮箱地址
     * @return 响应体
     */
    @Override
    public Response<Object> checkEmailUnique(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        Long result = userMapper.selectCount(queryWrapper);
        if (result.intValue() == 0) {
            return Response.success(200, "校验通过");
        }
        return Response.success(400, "邮箱已存在");
    }

    /**
     * 发送验证码到邮箱地址
     *
     * @param to        目标邮箱
     * @param emailCode 验证码
     * @return result
     */
    public Boolean sendEmailCode(String to, String emailCode) {
        Context context = new Context();
        context.setVariable("emailCode", emailCode);
        String emailContent = templateEngine.process("mail-register-template", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(emailContent, true);
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }
}
