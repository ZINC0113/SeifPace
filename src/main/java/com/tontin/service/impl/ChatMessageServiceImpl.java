package com.tontin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tontin.entity.ChatMessage;
import com.tontin.mapper.ChatMessageMapper;
import com.tontin.service.ChatMessageService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
    
    @Override
    public List<ChatMessage> getMessagesByUserId(Long userId, Long lastMessageId, Integer limit) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver_id", userId);
        if (lastMessageId != null) {
            wrapper.lt("id", lastMessageId);
        }
        wrapper.orderByDesc("create_time");
        wrapper.last("limit " + limit);
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public List<ChatMessage> getChatHistory(Long userId1, Long userId2, Long lastMessageId, Integer limit) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq("sender_id", userId1).eq("receiver_id", userId2)
                .or().eq("sender_id", userId2).eq("receiver_id", userId1));
        if (lastMessageId != null) {
            wrapper.lt("id", lastMessageId);
        }
        wrapper.orderByDesc("create_time");
        wrapper.last("limit " + limit);
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public List<ChatMessage> getTeamChatHistory(Long teamId, Long lastMessageId, Integer limit) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        if (lastMessageId != null) {
            wrapper.lt("id", lastMessageId);
        }
        wrapper.orderByDesc("create_time");
        wrapper.last("limit " + limit);
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public boolean markAsRead(Long messageId) {
        ChatMessage message = baseMapper.selectById(messageId);
        if (message != null) {
            message.setStatus(3); // 3表示已读
            return baseMapper.updateById(message) > 0;
        }
        return false;
    }
    
    @Override
    public boolean recallMessage(Long messageId, Long userId) {
        ChatMessage message = baseMapper.selectById(messageId);
        if (message != null && message.getSenderId().equals(userId)) {
            message.setStatus(0); // 0表示已撤回
            return baseMapper.updateById(message) > 0;
        }
        return false;
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver_id", userId);
        wrapper.eq("status", 1); // 1表示未读
        return baseMapper.selectCount(wrapper).intValue();
    }
}
