package com.tontin.controller;

import com.tontin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestParam String phone, 
                                       @RequestParam String password, 
                                       @RequestParam String nickname) {
        try {
            userService.register(phone, password, nickname);
            return Map.of("code", 200, "message", "注册成功");
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String phone, 
                                    @RequestParam String password) {
        try {
            Map<String, Object> result = userService.login(phone, password);
            return Map.of("code", 200, "message", "登录成功", "data", result);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 手机号+验证码登录
     */
    @PostMapping("/loginWithCode")
    public Map<String, Object> loginWithCode(@RequestParam String phone, 
                                            @RequestParam String code) {
        try {
            Map<String, Object> result = userService.loginWithCode(phone, code);
            return Map.of("code", 200, "message", "登录成功", "data", result);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 第三方登录
     */
    @PostMapping("/thirdPartyLogin")
    public Map<String, Object> thirdPartyLogin(@RequestParam String openId, 
                                             @RequestParam String platform, 
                                             @RequestParam String nickname, 
                                             @RequestParam String avatar) {
        try {
            Map<String, Object> result = userService.thirdPartyLogin(openId, platform, nickname, avatar);
            return Map.of("code", 200, "message", "登录成功", "data", result);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 发送验证码
     */
    @PostMapping("/sendCode")
    public Map<String, Object> sendCode(@RequestParam String phone) {
        try {
            userService.sendCode(phone);
            return Map.of("code", 200, "message", "验证码发送成功");
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 忘记密码
     */
    @PostMapping("/resetPassword")
    public Map<String, Object> resetPassword(@RequestParam String phone, 
                                           @RequestParam String code, 
                                           @RequestParam String newPassword) {
        try {
            userService.resetPassword(phone, code, newPassword);
            return Map.of("code", 200, "message", "密码重置成功");
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
}
