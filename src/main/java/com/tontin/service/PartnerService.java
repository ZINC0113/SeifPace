package com.tontin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tontin.entity.Partner;
import com.tontin.entity.User;
import java.util.List;

public interface PartnerService extends IService<Partner> {
    /**
     * 匹配搭子
     */
    User matchPartner(Long userId);
    
    /**
     * 获取用户的搭子列表
     */
    List<User> getPartners(Long userId);
    
    /**
     * 解除搭子关系
     */
    boolean removePartner(Long userId, Long partnerId);
    
    /**
     * 重新匹配搭子
     */
    User rematchPartner(Long userId);
    
    /**
     * 检查是否是搭子关系
     */
    boolean isPartner(Long userId1, Long userId2);
}
