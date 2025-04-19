package bbs.example.authservice.controller;

import com.example.common.response.Response;
import com.example.userservice.domain.po.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 账号 登录、注册、验证 控制器
 */
@RestController
public class AccountController {
    /**
     * 用户注册
     * @param user 用户实体类
     * @param request 请求对象
     * @param code 图形验证码
     * @param codeOwner 图形验证码所属者
     * @return result
     */
    @PostMapping("/register")
    public Response<Object> register(@RequestBody User user, HttpServletRequest request,
                                     @RequestParam String code, @RequestParam String codeOwner) {
        return null;
    }

    /**
     * 用户登录
     * @param user 用户
     * @param code 图形验证码
     * @param codeOwner 图形验证码所属者
     * @return result
     */
    @PostMapping("/login")
    public Response<Object> login(@RequestBody User user, @RequestParam String code, @RequestParam String codeOwner) {
        return null;
    }

    @GetMapping("/activate")
    public Response<String> getActivateEmailCode(@RequestParam String uid) {
        return null;
    }

    /**
     * 激活邮箱
     * @param user 用户
     * @param emailCode 邮箱验证码
     * @return result
     */
    @PutMapping("/activate")
    public Response<Object> activateEmail(@RequestBody User user, @RequestParam String emailCode) {
        return null;
    }

    /**
     * 刷新访问凭证
     * @param refreshToken 刷新凭证
     * @return result
     */
    @PostMapping("/refresh")
    public Response<Map<String, Object>> refreshToken(@RequestParam String refreshToken) {
        return null;
    }

    /**
     * 校验用户名唯一性
     * @param username 用户名
     * @return 响应体
     */
    @GetMapping("/_checkUsernameUnique")
    public Response<Object> checkUsernameUnique(@RequestParam String username) {
        return null;
    }

    /**
     * 获取 Hutool 图片验证码
     * @return 验证码响应体
     */
    @GetMapping("/_captcha")
    public Response<Map<String, String>> getCaptcha() {
        return null;
    }

    /**
     * 校验图形验证码输入是否正确
     * @param code 图形验证码
     * @param codeOwner 图形验证码所属者
     * @return 响应体
     */
    @GetMapping("/_checkCaptcha")
    public Response<Object> checkCaptcha(@RequestParam String code, @RequestParam String codeOwner) {
        return null;
    }
}
