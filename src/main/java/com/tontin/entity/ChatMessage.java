package com.tontin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("chat_message")
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long senderId;
    private Long receiverId;
    private Integer messageType;
    private String content;
    private Long teamId;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
