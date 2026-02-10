package com.tontin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tontin.entity.User;
import com.tontin.mapper.UserMapper;
import com.tontin.service.UserService;
import com.tontin.utils.JwtUtil;
import com.tontin.utils.RedisUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public User register(String phone, String password, String nickname) {
        // 检查手机号是否已注册
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("手机号已注册");
        }
        
        // 创建用户
        User user = new User();
        user.setPhone(phone);
        user.setPassword(DigestUtil.md5Hex(password));
        user.setNickname(nickname);
        user.setScore(0);
        user.setMemberLevel(0);
        user.setStatus(1);
        user.setLastLoginTime(new Date());
        
        baseMapper.insert(user);
        return user;
    }
    
    @Override
    public Map<String, Object> login(String phone, String password) {
        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        User user = baseMapper.selectOne(wrapper);
        
        if (user == null || !DigestUtil.md5Hex(password).equals(user.getPassword())) {
            throw new RuntimeException("手机号或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已禁用");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(new Date());
        baseMapper.updateById(user);
        
        // 生成token
        String token = jwtUtil.generateToken(user.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }
    
    @Override
    public Map<String, Object> loginWithCode(String phone, String code) {
        // 验证验证码
        String redisCode = redisUtil.get("code:" + phone);
        if (redisCode == null || !redisCode.equals(code)) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        User user = baseMapper.selectOne(wrapper);
        
        if (user == null) {
            // 自动注册
            user = new User();
            user.setPhone(phone);
            user.setPassword(DigestUtil.md5Hex(RandomUtil.randomString(8)));
            user.setNickname("用户" + phone.substring(7));
            user.setScore(0);
            user.setMemberLevel(0);
            user.setStatus(1);
            user.setLastLoginTime(new Date());
            baseMapper.insert(user);
        } else {
            // 更新最后登录时间
            user.setLastLoginTime(new Date());
            baseMapper.updateById(user);
        }
        
        // 生成token
        String token = jwtUtil.generateToken(user.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }
    
    @Override
    public Map<String, Object> thirdPartyLogin(String openId, String platform, String nickname, String avatar) {
        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", platform + "_" + openId); // 第三方登录使用平台+openId作为手机号
        User user = baseMapper.selectOne(wrapper);
        
        if (user == null) {
            // 自动注册
            user = new User();
            user.setPhone(platform + "_" + openId);
            user.setPassword(DigestUtil.md5Hex(RandomUtil.randomString(8)));
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setScore(0);
            user.setMemberLevel(0);
            user.setStatus(1);
            user.setLastLoginTime(new Date());
            baseMapper.insert(user);
        } else {
            // 更新最后登录时间
            user.setLastLoginTime(new Date());
            baseMapper.updateById(user);
        }
        
        // 生成token
        String token = jwtUtil.generateToken(user.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }
    
    @Override
    public boolean sendCode(String phone) {
        // 生成验证码
        String code = RandomUtil.randomNumbers(6);
        
        // 存入Redis，有效期5分钟
        redisUtil.set("code:" + phone, code, 300, TimeUnit.SECONDS);
        
        // 这里应该调用短信服务发送验证码
        System.out.println("向" + phone + "发送验证码：" + code);
        return true;
    }
    
    @Override
    public boolean resetPassword(String phone, String code, String newPassword) {
        // 验证验证码
        String redisCode = redisUtil.get("code:" + phone);
        if (redisCode == null || !redisCode.equals(code)) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 查找用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        User user = baseMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新密码
        user.setPassword(DigestUtil.md5Hex(newPassword));
        baseMapper.updateById(user);
        return true;
    }
}
