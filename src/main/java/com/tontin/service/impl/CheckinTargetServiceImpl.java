package com.tontin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tontin.entity.CheckinTarget;
import com.tontin.mapper.CheckinTargetMapper;
import com.tontin.service.CheckinTargetService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class CheckinTargetServiceImpl extends ServiceImpl<CheckinTargetMapper, CheckinTarget> implements CheckinTargetService {
    
    @Override
    public List<CheckinTarget> getTargetsByUserId(Long userId) {
        QueryWrapper<CheckinTarget> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 1);
        wrapper.orderByDesc("create_time");
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public CheckinTarget createTarget(CheckinTarget target) {
        target.setStatus(1);
        baseMapper.insert(target);
        return target;
    }
    
    @Override
    public boolean updateTarget(CheckinTarget target) {
        CheckinTarget existingTarget = baseMapper.selectById(target.getId());
        if (existingTarget != null && existingTarget.getUserId().equals(target.getUserId())) {
            return baseMapper.updateById(target) > 0;
        }
        return false;
    }
    
    @Override
    public boolean deleteTarget(Long targetId, Long userId) {
        CheckinTarget target = baseMapper.selectById(targetId);
        if (target != null && target.getUserId().equals(userId)) {
            target.setStatus(0);
            return baseMapper.updateById(target) > 0;
        }
        return false;
    }
    
    @Override
    public List<CheckinTarget> getTodayTargets(Long userId) {
        Date today = new Date();
        QueryWrapper<CheckinTarget> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 1);
        wrapper.le("start_time", today);
        wrapper.or().isNull("end_time");
        wrapper.orderByDesc("create_time");
        return baseMapper.selectList(wrapper);
    }
}
