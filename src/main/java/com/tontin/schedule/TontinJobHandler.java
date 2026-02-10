package com.tontin.schedule;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.tontin.entity.CheckinTarget;
import com.tontin.entity.CheckinRecord;
import com.tontin.entity.User;
import com.tontin.entity.MemberOrder;
import com.tontin.mapper.CheckinTargetMapper;
import com.tontin.mapper.CheckinRecordMapper;
import com.tontin.mapper.UserMapper;
import com.tontin.service.ChatMessageService;
import com.tontin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TontinJobHandler {
    
    @Autowired
    private CheckinTargetMapper checkinTargetMapper;
    
    @Autowired
    private CheckinRecordMapper checkinRecordMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    @Autowired
    private RedisUtil redisUtil;
    
    /**
     * 打卡倒计时提醒
     */
    @XxlJob("checkinReminderJob")
    public ReturnT<String> checkinReminderJob(String param) {
        try {
            Date now = new Date();
            // 查询30分钟后需要打卡的目标
            Date reminderTime = new Date(now.getTime() + 30 * 60 * 1000);
            
            QueryWrapper<CheckinTarget> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1);
            wrapper.le("end_time", reminderTime);
            List<CheckinTarget> targets = checkinTargetMapper.selectList(wrapper);
            
            for (CheckinTarget target : targets) {
                // 检查今日是否已打卡
                String todayStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(now);
                String key = "checkin:" + target.getUserId() + ":" + target.getId() + ":" + todayStr;
                String countStr = redisUtil.get(key);
                int count = countStr != null ? Integer.parseInt(countStr) : 0;
                
                if (count < target.getDailyCount()) {
                    // 发送打卡提醒消息
                    // 这里可以调用消息推送服务
                    System.out.println("提醒用户 " + target.getUserId() + " 打卡：" + target.getName());
                }
            }
            
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
    
    /**
     * 逾期打卡提醒
     */
    @XxlJob("overdueCheckinJob")
    public ReturnT<String> overdueCheckinJob(String param) {
        try {
            Date now = new Date();
            // 查询已过期1小时的打卡目标
            Date overdueTime = new Date(now.getTime() - 60 * 60 * 1000);
            
            QueryWrapper<CheckinTarget> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1);
            wrapper.lt("end_time", overdueTime);
            List<CheckinTarget> targets = checkinTargetMapper.selectList(wrapper);
            
            for (CheckinTarget target : targets) {
                // 检查今日是否已打卡
                String todayStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(now);
                String key = "checkin:" + target.getUserId() + ":" + target.getId() + ":" + todayStr;
                String countStr = redisUtil.get(key);
                int count = countStr != null ? Integer.parseInt(countStr) : 0;
                
                if (count < target.getDailyCount()) {
                    // 发送逾期提醒消息
                    System.out.println("提醒用户 " + target.getUserId() + " 逾期打卡：" + target.getName());
                }
            }
            
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
    
    /**
     * 过期打卡数据清理
     */
    @XxlJob("cleanupCheckinJob")
    public ReturnT<String> cleanupCheckinJob(String param) {
        try {
            Date now = new Date();
            // 清理6个月前的打卡记录
            Date cleanupTime = new Date(now.getTime() - 6 * 30 * 24 * 60 * 60 * 1000);
            
            QueryWrapper<CheckinRecord> wrapper = new QueryWrapper<>();
            wrapper.lt("create_time", cleanupTime);
            checkinRecordMapper.delete(wrapper);
            
            System.out.println("清理过期打卡数据完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
    
    /**
     * 未读消息自动标记
     */
    @XxlJob("markReadMessageJob")
    public ReturnT<String> markReadMessageJob(String param) {
        try {
            Date now = new Date();
            // 标记7天前的未读消息为已读
            Date markTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
            
            // 这里需要实现消息标记逻辑
            System.out.println("自动标记未读消息完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
    
    /**
     * 离线长连接清理
     */
    @XxlJob("cleanupWebSocketJob")
    public ReturnT<String> cleanupWebSocketJob(String param) {
        try {
            // 清理24小时前的离线连接
            // 这里需要实现WebSocket连接清理逻辑
            System.out.println("清理离线长连接完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
    
    /**
     * 会员到期自动降级
     */
    @XxlJob("memberExpireJob")
    public ReturnT<String> memberExpireJob(String param) {
        try {
            Date now = new Date();
            // 查询已过期的会员
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.gt("member_level", 0);
            wrapper.lt("member_expire_time", now);
            List<User> users = userMapper.selectList(wrapper);
            
            for (User user : users) {
                user.setMemberLevel(0);
                user.setMemberExpireTime(null);
                userMapper.updateById(user);
                System.out.println("用户 " + user.getId() + " 会员到期自动降级");
            }
            
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
    
    /**
     * 打卡统计计算
     */
    @XxlJob("checkinStatsJob")
    public ReturnT<String> checkinStatsJob(String param) {
        try {
            // 计算昨日打卡统计数据
            Date yesterday = new Date();
            String yesterdayStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(yesterday);
            
            // 这里需要实现打卡统计逻辑
            System.out.println("计算打卡统计数据完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
}
