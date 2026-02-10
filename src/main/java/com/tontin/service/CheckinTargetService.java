package com.tontin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tontin.entity.CheckinTarget;
import java.util.List;

public interface CheckinTargetService extends IService<CheckinTarget> {
    /**
     * 获取用户的打卡目标列表
     */
    List<CheckinTarget> getTargetsByUserId(Long userId);
    
    /**
     * 创建打卡目标
     */
    CheckinTarget createTarget(CheckinTarget target);
    
    /**
     * 更新打卡目标
     */
    boolean updateTarget(CheckinTarget target);
    
    /**
     * 删除打卡目标
     */
    boolean deleteTarget(Long targetId, Long userId);
    
    /**
     * 获取今日待打卡目标
     */
    List<CheckinTarget> getTodayTargets(Long userId);
}
