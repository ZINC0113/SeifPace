package com.tontin.websocket;

import com.tontin.utils.RedisUtil;
import com.tontin.entity.ChatMessage;
import com.tontin.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    // 存储WebSocket会话，key为userId
    private static final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从session中获取userId
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            // 存储会话
            sessions.put(userId, session);
            // 更新Redis中的在线状态
            redisUtil.set("online:" + userId, "1");
            // 通知其他用户该用户上线
            broadcastOnlineStatus(userId, true);
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 从session中获取userId
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            return;
        }
        
        // 解析消息
        JSONObject jsonObject = JSON.parseObject(message.getPayload());
        String type = jsonObject.getString("type");
        
        switch (type) {
            case "chat":
                // 处理聊天消息
                handleChatMessage(userId, jsonObject);
                break;
            case "ping":
                // 处理心跳
                session.sendMessage(new TextMessage(JSON.toJSONString("{\"type\": \"pong\"}")));
                break;
            case "read":
                // 处理已读消息
                handleReadMessage(userId, jsonObject);
                break;
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 从session中获取userId
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            // 移除会话
            sessions.remove(userId);
            // 更新Redis中的在线状态
            redisUtil.set("online:" + userId, "0");
            // 通知其他用户该用户下线
            broadcastOnlineStatus(userId, false);
        }
    }
    
    /**
     * 处理聊天消息
     */
    private void handleChatMessage(Long userId, JSONObject jsonObject) {
        // 构建消息对象
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(userId);
        chatMessage.setReceiverId(jsonObject.getLong("receiverId"));
        chatMessage.setMessageType(jsonObject.getInteger("messageType"));
        chatMessage.setContent(jsonObject.getString("content"));
        chatMessage.setTeamId(jsonObject.getLong("teamId"));
        chatMessage.setStatus(1);
        
        // 保存消息到数据库
        chatMessageService.save(chatMessage);
        
        // 尝试发送消息给接收者
        WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            try {
                JSONObject response = new JSONObject();
                response.put("type", "chat");
                response.put("message", chatMessage);
                receiverSession.sendMessage(new TextMessage(response.toJSONString()));
                // 更新消息状态为已送达
                chatMessage.setStatus(2);
                chatMessageService.updateById(chatMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 处理已读消息
     */
    private void handleReadMessage(Long userId, JSONObject jsonObject) {
        Long messageId = jsonObject.getLong("messageId");
        // 更新消息状态为已读
        chatMessageService.markAsRead(messageId);
    }
    
    /**
     * 广播在线状态
     */
    private void broadcastOnlineStatus(Long userId, boolean online) {
        // 这里可以根据业务逻辑，通知该用户的搭子、组队成员等
        // 简化实现，仅做示例
    }
    
    /**
     * 发送消息给指定用户
     */
    public void sendMessageToUser(Long userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
