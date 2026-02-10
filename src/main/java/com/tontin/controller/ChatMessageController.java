package com.tontin.controller;

import com.tontin.service.ChatMessageService;
import com.tontin.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatMessageController {
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    /**
     * 获取用户的消息列表
     */
    @GetMapping("/messages")
    public Map<String, Object> getMessages(@RequestAttribute Long userId, 
                                          @RequestParam(required = false) Long lastMessageId, 
                                          @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<ChatMessage> messages = chatMessageService.getMessagesByUserId(userId, lastMessageId, limit);
            return Map.of("code", 200, "message", "获取成功", "data", messages);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 获取与指定用户的聊天历史
     */
    @GetMapping("/history")
    public Map<String, Object> getChatHistory(@RequestAttribute Long userId, 
                                             @RequestParam Long otherUserId, 
                                             @RequestParam(required = false) Long lastMessageId, 
                                             @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<ChatMessage> messages = chatMessageService.getChatHistory(userId, otherUserId, lastMessageId, limit);
            return Map.of("code", 200, "message", "获取成功", "data", messages);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 获取组队聊天历史
     */
    @GetMapping("/teamHistory")
    public Map<String, Object> getTeamChatHistory(@RequestAttribute Long userId, 
                                                 @RequestParam Long teamId, 
                                                 @RequestParam(required = false) Long lastMessageId, 
                                                 @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<ChatMessage> messages = chatMessageService.getTeamChatHistory(teamId, lastMessageId, limit);
            return Map.of("code", 200, "message", "获取成功", "data", messages);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     */
    @PostMapping("/markRead")
    public Map<String, Object> markAsRead(@RequestAttribute Long userId, 
                                         @RequestParam Long messageId) {
        try {
            chatMessageService.markAsRead(messageId);
            return Map.of("code", 200, "message", "标记成功");
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 撤回消息
     */
    @PostMapping("/recall")
    public Map<String, Object> recallMessage(@RequestAttribute Long userId, 
                                           @RequestParam Long messageId) {
        try {
            chatMessageService.recallMessage(messageId, userId);
            return Map.of("code", 200, "message", "撤回成功");
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
    
    /**
     * 获取未读消息数
     */
    @GetMapping("/unreadCount")
    public Map<String, Object> getUnreadCount(@RequestAttribute Long userId) {
        try {
            int count = chatMessageService.getUnreadCount(userId);
            return Map.of("code", 200, "message", "获取成功", "data", count);
        } catch (Exception e) {
            return Map.of("code", 400, "message", e.getMessage());
        }
    }
}
