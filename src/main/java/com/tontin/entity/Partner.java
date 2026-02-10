package com.tontin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("partner")
public class Partner {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long partnerId;
    private Date matchTime;
    private Integer status;
}