package com.tontin.controller;

import com.tontin.service.PartnerService;
import com.tontin.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/partner")
public class PartnerController {
    
    @Autowired
    private PartnerService partnerService;
    
    /**
     * 匹配搭子
     */
    @PostMapping("/match")
    public Map<String, Object> matchPartner(@RequestAttribute Long userId) {
        try {
            User partner = partnerService.matchPartner(userId);
            return Map.of("code", 200, "message", "匹配成功", "data", partner);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 获取搭子列表
     */
    @GetMapping("/list")
    public Map<String, Object> getPartners(@RequestAttribute Long userId) {
        try {
            List<User> partners = partnerService.getPartners(userId);
            return Map.of("code", 200, "message", "获取成功", "data", partners);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 解除搭子关系
     */
    @PostMapping("/remove")
    public Map<String, Object> removePartner(@RequestAttribute Long userId, 
                                           @RequestParam Long partnerId) {
        try {
            boolean success = partnerService.removePartner(userId, partnerId);
            if (success) {
                return Map.of("code", 200, "message", "解除成功");
            } else {
                return Map.of("code", 400, "message", "解除失败");
            }
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 重新匹配搭子
     */
    @PostMapping("/rematch")
    public Map<String, Object> rematchPartner(@RequestAttribute Long userId) {
        try {
            User partner = partnerService.rematchPartner(userId);
            return Map.of("code", 200, "message", "重新匹配成功", "data", partner);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 检查是否是搭子关系
     */
    @GetMapping("/check")
    public Map<String, Object> checkPartner(@RequestAttribute Long userId, 
                                           @RequestParam Long otherUserId) {
        try {
            boolean isPartner = partnerService.isPartner(userId, otherUserId);
            return Map.of("code", 200, "message", "检查成功", "data", isPartner);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
}
