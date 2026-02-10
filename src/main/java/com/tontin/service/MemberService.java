package com.tontin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tontin.entity.MemberOrder;
import java.util.Map;

public interface MemberService extends IService<MemberOrder> {
    /**
     * 创建会员订单
     */
    MemberOrder createOrder(Long userId, Integer memberType);
    
    /**
     * 支付订单
     */
    boolean payOrder(String orderNo, Integer payType);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(String orderNo);
    
    /**
     * 获取用户的会员订单列表
     */
    Map<String, Object> getOrders(Long userId, Integer page, Integer size);
    
    /**
     * 检查用户会员状态
     */
    Map<String, Object> checkMemberStatus(Long userId);
    
    /**
     * 续费会员
     */
    MemberOrder renewMember(Long userId, Integer memberType);
}
