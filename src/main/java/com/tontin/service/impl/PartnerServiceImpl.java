package com.tontin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tontin.entity.Partner;
import com.tontin.entity.User;
import com.tontin.mapper.PartnerMapper;
import com.tontin.mapper.UserMapper;
import com.tontin.service.PartnerService;
import com.tontin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class PartnerServiceImpl extends ServiceImpl<PartnerMapper, Partner> implements PartnerService {
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User matchPartner(Long userId) {
        // 检查用户是否已经有搭子
        List<User> existingPartners = getPartners(userId);
        if (!existingPartners.isEmpty()) {
            return existingPartners.get(0);
        }
        
        // 获取用户积分
        User user = userMapper.selectById(userId);
        int score = user.getScore() != null ? user.getScore() : 0;
        
        // 根据积分分组
        int scoreGroup = score / 100; // 每100积分一组
        
        // 从同积分组中随机选择一个用户作为搭子
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ne("id", userId);
        wrapper.ge("score", scoreGroup * 100);
        wrapper.lt("score", (scoreGroup + 1) * 100);
        wrapper.eq("status", 1);
        List<User> candidates = userMapper.selectList(wrapper);
        
        if (candidates.isEmpty()) {
            throw new RuntimeException("暂无合适的搭子");
        }
        
        // 随机选择一个候选用户
        Random random = new Random();
        User partner = candidates.get(random.nextInt(candidates.size()));
        
        // 创建搭子关系
        Partner partnerRelation1 = new Partner();
        partnerRelation1.setUserId(userId);
        partnerRelation1.setPartnerId(partner.getId());
        partnerRelation1.setMatchTime(new Date());
        partnerRelation1.setStatus(1);
        baseMapper.insert(partnerRelation1);
        
        Partner partnerRelation2 = new Partner();
        partnerRelation2.setUserId(partner.getId());
        partnerRelation2.setPartnerId(userId);
        partnerRelation2.setMatchTime(new Date());
        partnerRelation2.setStatus(1);
        baseMapper.insert(partnerRelation2);
        
        return partner;
    }
    
    @Override
    public List<User> getPartners(Long userId) {
        QueryWrapper<Partner> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 1);
        List<Partner> partnerRelations = baseMapper.selectList(wrapper);
        
        List<User> partners = new java.util.ArrayList<>();
        for (Partner relation : partnerRelations) {
            User partner = userMapper.selectById(relation.getPartnerId());
            if (partner != null) {
                partners.add(partner);
            }
        }
        
        return partners;
    }
    
    @Override
    public boolean removePartner(Long userId, Long partnerId) {
        // 解除用户的搭子关系
        QueryWrapper<Partner> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("user_id", userId);
        wrapper1.eq("partner_id", partnerId);
        Partner relation1 = baseMapper.selectOne(wrapper1);
        if (relation1 != null) {
            relation1.setStatus(0);
            baseMapper.updateById(relation1);
        }
        
        // 解除对方的搭子关系
        QueryWrapper<Partner> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("user_id", partnerId);
        wrapper2.eq("partner_id", userId);
        Partner relation2 = baseMapper.selectOne(wrapper2);
        if (relation2 != null) {
            relation2.setStatus(0);
            baseMapper.updateById(relation2);
        }
        
        return true;
    }
    
    @Override
    public User rematchPartner(Long userId) {
        // 先解除现有的搭子关系
        List<User> existingPartners = getPartners(userId);
        for (User partner : existingPartners) {
            removePartner(userId, partner.getId());
        }
        
        // 重新匹配
        return matchPartner(userId);
    }
    
    @Override
    public boolean isPartner(Long userId1, Long userId2) {
        QueryWrapper<Partner> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId1);
        wrapper.eq("partner_id", userId2);
        wrapper.eq("status", 1);
        return baseMapper.selectCount(wrapper) > 0;
    }
}
