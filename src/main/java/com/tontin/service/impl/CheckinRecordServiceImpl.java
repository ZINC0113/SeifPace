package com.tontin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tontin.entity.CheckinRecord;
import com.tontin.entity.CheckinTarget;
import com.tontin.mapper.CheckinRecordMapper;
import com.tontin.mapper.CheckinTargetMapper;
import com.tontin.service.CheckinRecordService;
import com.tontin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CheckinRecordServiceImpl extends ServiceImpl<CheckinRecordMapper, CheckinRecord> implements CheckinRecordService {
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private CheckinTargetMapper checkinTargetMapper;
    
    @Override
    public CheckinRecord doCheckin(CheckinRecord record) {
        // 验证目标是否存在
        CheckinTarget target = checkinTargetMapper.selectById(record.getTargetId());
        if (target == null || !target.getUserId().equals(record.getUserId())) {
            throw new RuntimeException("打卡目标不存在");
        }
        
        // 检查今日是否已达到打卡次数上限
        Date today = new Date();
        String todayStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(today);
        String key = "checkin:" + record.getUserId() + ":" + record.getTargetId() + ":" + todayStr;
        String countStr = redisUtil.get(key);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;
        
        if (count >= target.getDailyCount()) {
            throw new RuntimeException("今日打卡次数已达到上限");
        }
        
        // 执行打卡
        record.setCheckinTime(new Date());
        record.setStatus(1);
        baseMapper.insert(record);
        
        // 更新Redis中的打卡次数
        redisUtil.set(key, String.valueOf(count + 1), 24, TimeUnit.HOURS);
        
        // 更新用户积分
        // 这里可以根据业务逻辑更新用户积分
        
        return record;
    }
    
    @Override
    public Map<String, Object> startTimedCheckin(Long userId, Long targetId) {
        // 验证目标是否存在
        CheckinTarget target = checkinTargetMapper.selectById(targetId);
        if (target == null || !target.getUserId().equals(userId)) {
            throw new RuntimeException("打卡目标不存在");
        }
        
        // 检查是否已经在进行限时打卡
        String key = "timed_checkin:" + userId + ":" + targetId;
        if (redisUtil.exists(key)) {
            throw new RuntimeException("已经在进行限时打卡");
        }
        
        // 开始限时打卡
        Date startTime = new Date();
        redisUtil.set(key, String.valueOf(startTime.getTime()), 2, TimeUnit.HOURS); // 最多2小时
        
        Map<String, Object> result = new HashMap<>();
        result.put("startTime", startTime);
        result.put("message", "限时打卡开始");
        return result;
    }
    
    @Override
    public CheckinRecord endTimedCheckin(Long userId, Long targetId) {
        // 检查是否在进行限时打卡
        String key = "timed_checkin:" + userId + ":" + targetId;
        String startTimeStr = redisUtil.get(key);
        if (startTimeStr == null) {
            throw new RuntimeException("未开始限时打卡");
        }
        
        // 计算打卡时长
        long startTime = Long.parseLong(startTimeStr);
        long endTime = System.currentTimeMillis();
        int duration = (int) ((endTime - startTime) / 1000);
        
        // 清除Redis中的打卡记录
        redisUtil.delete(key);
        
        // 创建打卡记录
        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setTargetId(targetId);
        record.setCheckinTime(new Date());
        record.setCheckinType(2); // 2表示限时打卡
        record.setDuration(duration);
        record.setStatus(1);
        
        baseMapper.insert(record);
        
        // 更新用户积分
        // 这里可以根据业务逻辑更新用户积分
        
        return record;
    }
    
    @Override
    public List<CheckinRecord> getRecordsByUserId(Long userId, Long targetId, String startDate, String endDate) {
        QueryWrapper<CheckinRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (targetId != null) {
            wrapper.eq("target_id", targetId);
        }
        if (startDate != null) {
            wrapper.ge("checkin_time", startDate);
        }
        if (endDate != null) {
            wrapper.le("checkin_time", endDate + " 23:59:59");
        }
        wrapper.orderByDesc("checkin_time");
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public Map<String, Object> getCheckinStats(Long userId, String startDate, String endDate) {
        // 这里可以根据业务逻辑实现打卡统计
        // 例如：总打卡次数、连续打卡天数、各类型打卡统计等
        Map<String, Object> stats = new HashMap<>();
        
        // 示例：获取总打卡次数
        QueryWrapper<CheckinRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (startDate != null) {
            wrapper.ge("checkin_time", startDate);
        }
        if (endDate != null) {
            wrapper.le("checkin_time", endDate + " 23:59:59");
        }
        Long totalCountLong = baseMapper.selectCount(wrapper);
        int totalCount = totalCountLong.intValue();
        stats.put("totalCount", totalCount);
        
        return stats;
    }
    
    @Override
    public boolean validateLocation(Double latitude, Double longitude, Double targetLatitude, Double targetLongitude, double radius) {
        // 计算两点之间的距离（单位：米）
        double distance = calculateDistance(latitude, longitude, targetLatitude, targetLongitude);
        return distance <= radius;
    }
    
    /**
     * 计算两点之间的距离（单位：米）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // 地球半径（单位：米）
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
