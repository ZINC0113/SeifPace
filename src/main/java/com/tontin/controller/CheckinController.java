package com.tontin.controller;

import com.tontin.service.CheckinTargetService;
import com.tontin.service.CheckinRecordService;
import com.tontin.entity.CheckinTarget;
import com.tontin.entity.CheckinRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/checkin")
public class CheckinController {
    
    @Autowired
    private CheckinTargetService checkinTargetService;
    
    @Autowired
    private CheckinRecordService checkinRecordService;
    
    /**
     * 创建打卡目标
     */
    @PostMapping("/target/create")
    public Map<String, Object> createTarget(@RequestAttribute Long userId, 
                                           @RequestBody CheckinTarget target) {
        try {
            target.setUserId(userId);
            CheckinTarget createdTarget = checkinTargetService.createTarget(target);
            return Map.of("code", 200, "message", "创建成功", "data", createdTarget);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 更新打卡目标
     */
    @PostMapping("/target/update")
    public Map<String, Object> updateTarget(@RequestAttribute Long userId, 
                                           @RequestBody CheckinTarget target) {
        try {
            target.setUserId(userId);
            boolean success = checkinTargetService.updateTarget(target);
            if (success) {
                return Map.of("code", 200, "message", "更新成功");
            } else {
                return Map.of("code", 400, "message", "更新失败，目标不存在或无权限");
            }
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 删除打卡目标
     */
    @PostMapping("/target/delete")
    public Map<String, Object> deleteTarget(@RequestAttribute Long userId, 
                                           @RequestParam Long targetId) {
        try {
            boolean success = checkinTargetService.deleteTarget(targetId, userId);
            if (success) {
                return Map.of("code", 200, "message", "删除成功");
            } else {
                return Map.of("code", 400, "message", "删除失败，目标不存在或无权限");
            }
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 获取打卡目标列表
     */
    @GetMapping("/target/list")
    public Map<String, Object> getTargets(@RequestAttribute Long userId) {
        try {
            return Map.of("code", 200, "message", "获取成功", "data", checkinTargetService.getTargetsByUserId(userId));
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 获取今日待打卡目标
     */
    @GetMapping("/target/today")
    public Map<String, Object> getTodayTargets(@RequestAttribute Long userId) {
        try {
            return Map.of("code", 200, "message", "获取成功", "data", checkinTargetService.getTodayTargets(userId));
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 执行打卡
     */
    @PostMapping("/doCheckin")
    public Map<String, Object> doCheckin(@RequestAttribute Long userId, 
                                        @RequestBody CheckinRecord record) {
        try {
            record.setUserId(userId);
            CheckinRecord createdRecord = checkinRecordService.doCheckin(record);
            return Map.of("code", 200, "message", "打卡成功", "data", createdRecord);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 开始限时打卡
     */
    @PostMapping("/startTimedCheckin")
    public Map<String, Object> startTimedCheckin(@RequestAttribute Long userId, 
                                                @RequestParam Long targetId) {
        try {
            Map<String, Object> result = checkinRecordService.startTimedCheckin(userId, targetId);
            return Map.of("code", 200, "message", "开始限时打卡成功", "data", result);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 结束限时打卡
     */
    @PostMapping("/endTimedCheckin")
    public Map<String, Object> endTimedCheckin(@RequestAttribute Long userId, 
                                              @RequestParam Long targetId) {
        try {
            CheckinRecord record = checkinRecordService.endTimedCheckin(userId, targetId);
            return Map.of("code", 200, "message", "结束限时打卡成功", "data", record);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 获取打卡记录
     */
    @GetMapping("/records")
    public Map<String, Object> getRecords(@RequestAttribute Long userId, 
                                         @RequestParam(required = false) Long targetId, 
                                         @RequestParam(required = false) String startDate, 
                                         @RequestParam(required = false) String endDate) {
        try {
            return Map.of("code", 200, "message", "获取成功", "data", 
                    checkinRecordService.getRecordsByUserId(userId, targetId, startDate, endDate));
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 获取打卡统计
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats(@RequestAttribute Long userId, 
                                       @RequestParam(required = false) String startDate, 
                                       @RequestParam(required = false) String endDate) {
        try {
            return Map.of("code", 200, "message", "获取成功", "data", 
                    checkinRecordService.getCheckinStats(userId, startDate, endDate));
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
}
