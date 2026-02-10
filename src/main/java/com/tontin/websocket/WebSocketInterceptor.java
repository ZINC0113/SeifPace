package com.tontin.websocket;

import com.tontin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class WebSocketInterceptor extends HttpSessionHandshakeInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
   public boolean beforeHandshake(HttpServletRequest request, HttpServletResponse response,
                              org.springframework.web.socket.WebSocketHandler wsHandler, 
                              Map<String, Object> attributes) throws Exception {

    // 从请求参数获取token
    String token = request.getParameter("token");
    if (token != null && jwtUtil.verifyToken(token)) {
        // 验证成功，获取userId并设置到attributes中
        Long userId = jwtUtil.getUserIdFromToken(token);
        attributes.put("userId", userId);
        return true;  // 允许WebSocket连接
    }
    // token无效，拒绝连接
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return false;  // 拒绝WebSocket连接
    }
}
