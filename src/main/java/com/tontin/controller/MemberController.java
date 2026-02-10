package com.tontin.controller;

import com.tontin.service.MemberService;
import com.tontin.entity.MemberOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    /**
     * 创建会员订单
     */
    @PostMapping("/createOrder")
    public Map<String, Object> createOrder(@RequestAttribute Long userId, 
                                          @RequestParam Integer memberType) {
        try {
            MemberOrder order = memberService.createOrder(userId, memberType);
            return Map.of("code", 200, "message", "订单创建成功", "data", order);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/payOrder")
    public Map<String, Object> payOrder(@RequestAttribute Long userId, 
                                       @RequestParam String orderNo, 
                                       @RequestParam Integer payType) {
        try {
            boolean success = memberService.payOrder(orderNo, payType);
            if (success) {
                return Map.of("code", 200, "message", "支付成功");
            } else {
                return Map.of("code", 400, "message", "支付失败");
            }
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancelOrder")
    public Map<String, Object> cancelOrder(@RequestAttribute Long userId, 
                                          @RequestParam String orderNo) {
        try {
            boolean success = memberService.cancelOrder(orderNo);
            if (success) {
                return Map.of("code", 200, "message", "取消成功");
            } else {
                return Map.of("code", 400, "message", "取消失败");
            }
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 获取订单列表
     */
    @GetMapping("/orders")
    public Map<String, Object> getOrders(@RequestAttribute Long userId, 
                                        @RequestParam(defaultValue = "1") Integer page, 
                                        @RequestParam(defaultValue = "10") Integer size) {
        try {
            Map<String, Object> orders = memberService.getOrders(userId, page, size);
            return Map.of("code", 200, "message", "获取成功", "data", orders);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 检查会员状态
     */
    @GetMapping("/status")
    public Map<String, Object> checkStatus(@RequestAttribute Long userId) {
        try {
            Map<String, Object> status = memberService.checkMemberStatus(userId);
            return Map.of("code", 200, "message", "获取成功", "data", status);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 续费会员
     */
    @PostMapping("/renew")
    public Map<String, Object> renewMember(@RequestAttribute Long userId, 
                                          @RequestParam Integer memberType) {
        try {
            MemberOrder order = memberService.renewMember(userId, memberType);
            return Map.of("code", 200, "message", "续费订单创建成功", "data", order);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
}
