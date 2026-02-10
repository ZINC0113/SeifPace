package com.tontin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tontin.entity.User;
import java.util.Map;

public interface UserService extends IService<User> {
    /**
     * 用户注册
     */
    User register(String phone, String password, String nickname);
    
    /**
     * 用户登录
     */
    Map<String, Object> login(String phone, String password);
    
    /**
     * 手机号+验证码登录
     */
    Map<String, Object> loginWithCode(String phone, String code);
    
    /**
     * 第三方登录
     */
    Map<String, Object> thirdPartyLogin(String openId, String platform, String nickname, String avatar);
    
    /**
     * 发送验证码
     */
    boolean sendCode(String phone);
    
    /**
     * 忘记密码
     */
    boolean resetPassword(String phone, String code, String newPassword);
}
