package com.tontin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tontin.entity.CheckinRecord;
import java.util.List;
import java.util.Map;

public interface CheckinRecordService extends IService<CheckinRecord> {
    /**
     * 执行打卡
     */
    CheckinRecord doCheckin(CheckinRecord record);
    
    /**
     * 开始限时打卡
     */
    Map<String, Object> startTimedCheckin(Long userId, Long targetId);
    
    /**
     * 结束限时打卡
     */
    CheckinRecord endTimedCheckin(Long userId, Long targetId);
    
    /**
     * 获取用户的打卡记录
     */
    List<CheckinRecord> getRecordsByUserId(Long userId, Long targetId, String startDate, String endDate);
    
    /**
     * 获取打卡统计
     */
    Map<String, Object> getCheckinStats(Long userId, String startDate, String endDate);
    
    /**
     * 验证打卡位置
     */
    boolean validateLocation(Double latitude, Double longitude, Double targetLatitude, Double targetLongitude, double radius);
}
