package com.tontin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tontin.entity.MemberOrder;
import com.tontin.entity.User;
import com.tontin.mapper.MemberOrderMapper;
import com.tontin.mapper.UserMapper;
import com.tontin.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Random;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberOrderMapper, MemberOrder> implements MemberService {
    
    @Autowired
    private UserMapper userMapper;
    
    // 会员价格配置
    private static final Map<Integer, BigDecimal> MEMBER_PRICES = new HashMap<>();
    static {
        MEMBER_PRICES.put(1, new BigDecimal(19.9)); // 月度会员
        MEMBER_PRICES.put(2, new BigDecimal(49.9)); // 季度会员
        MEMBER_PRICES.put(3, new BigDecimal(149.9)); // 年度会员
    }
    
    // 会员有效期配置（天）
    private static final Map<Integer, Integer> MEMBER_DAYS = new HashMap<>();
    static {
        MEMBER_DAYS.put(1, 30); // 月度会员
        MEMBER_DAYS.put(2, 90); // 季度会员
        MEMBER_DAYS.put(3, 365); // 年度会员
    }
    
    @Override
    public MemberOrder createOrder(Long userId, Integer memberType) {
        // 验证会员类型
        if (!MEMBER_PRICES.containsKey(memberType)) {
            throw new RuntimeException("无效的会员类型");
        }
        
        // 生成订单号
        String orderNo = generateOrderNo();
        
        // 创建订单
        MemberOrder order = new MemberOrder();
        order.setUserId(userId);
        order.setOrderNo(orderNo);
        order.setMemberType(memberType);
        order.setAmount(MEMBER_PRICES.get(memberType));
        order.setStatus(1); // 待支付
        baseMapper.insert(order);
        
        return order;
    }
    
    @Override
    public boolean payOrder(String orderNo, Integer payType) {
        // 查找订单
        QueryWrapper<MemberOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        MemberOrder order = baseMapper.selectOne(wrapper);
        
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态错误");
        }
        
        // 更新订单状态
        order.setStatus(2); // 已支付
        order.setPayType(payType);
        order.setPayTime(new Date());
        baseMapper.updateById(order);
        
        // 更新用户会员状态
        User user = userMapper.selectById(order.getUserId());
        user.setMemberLevel(order.getMemberType());
        
        // 计算会员到期时间
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + MEMBER_DAYS.get(order.getMemberType()) * 24 * 60 * 60 * 1000);
        user.setMemberExpireTime(expireTime);
        userMapper.updateById(user);
        
        return true;
    }
    
    @Override
    public boolean cancelOrder(String orderNo) {
        // 查找订单
        QueryWrapper<MemberOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        MemberOrder order = baseMapper.selectOne(wrapper);
        
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态错误");
        }
        
        // 更新订单状态
        order.setStatus(3); // 已取消
        return baseMapper.updateById(order) > 0;
    }
    
    @Override
    public Map<String, Object> getOrders(Long userId, Integer page, Integer size) {
        QueryWrapper<MemberOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        
        // 计算分页
        int offset = (page - 1) * size;
        wrapper.last("limit " + offset + ", " + size);
        
        List<MemberOrder> orders = baseMapper.selectList(wrapper);
        long totalLong = baseMapper.selectCount(new QueryWrapper<MemberOrder>().eq("user_id", userId));
        int total = (int) totalLong;
        
        Map<String, Object> result = new HashMap<>();
        result.put("orders", orders);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return result;
    }
    
    @Override
    public Map<String, Object> checkMemberStatus(Long userId) {
        User user = userMapper.selectById(userId);
        Map<String, Object> status = new HashMap<>();
        
        if (user.getMemberLevel() > 0 && user.getMemberExpireTime() != null) {
            Date now = new Date();
            boolean isExpired = now.after(user.getMemberExpireTime());
            
            status.put("isMember", !isExpired);
            status.put("memberLevel", user.getMemberLevel());
            status.put("expireTime", user.getMemberExpireTime());
            status.put("daysRemaining", isExpired ? 0 : (int) ((user.getMemberExpireTime().getTime() - now.getTime()) / (24 * 60 * 60 * 1000)));
        } else {
            status.put("isMember", false);
            status.put("memberLevel", 0);
            status.put("expireTime", null);
            status.put("daysRemaining", 0);
        }
        
        return status;
    }
    
    @Override
    public MemberOrder renewMember(Long userId, Integer memberType) {
        // 验证会员类型
        if (!MEMBER_PRICES.containsKey(memberType)) {
            throw new RuntimeException("无效的会员类型");
        }
        
        // 生成订单号
        String orderNo = generateOrderNo();
        
        // 创建订单
        MemberOrder order = new MemberOrder();
        order.setUserId(userId);
        order.setOrderNo(orderNo);
        order.setMemberType(memberType);
        order.setAmount(MEMBER_PRICES.get(memberType));
        order.setStatus(1); // 待支付
        baseMapper.insert(order);
        
        return order;
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date());
        String randomStr = String.format("%06d", new Random().nextInt(1000000));
        return "M" + dateStr + randomStr;
    }
}
