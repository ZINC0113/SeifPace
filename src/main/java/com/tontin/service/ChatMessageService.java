package com.tontin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tontin.entity.ChatMessage;
import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {
    /**
     * 获取用户的聊天消息列表
     */
    List<ChatMessage> getMessagesByUserId(Long userId, Long lastMessageId, Integer limit);
    
    /**
     * 获取用户与指定用户的聊天记录
     */
    List<ChatMessage> getChatHistory(Long userId1, Long userId2, Long lastMessageId, Integer limit);
    
    /**
     * 获取组队聊天记录
     */
    List<ChatMessage> getTeamChatHistory(Long teamId, Long lastMessageId, Integer limit);
    
    /**
     * 标记消息为已读
     */
    boolean markAsRead(Long messageId);
    
    /**
     * 撤回消息
     */
    boolean recallMessage(Long messageId, Long userId);
    
    /**
     * 获取未读消息数
     */
    int getUnreadCount(Long userId);
}
