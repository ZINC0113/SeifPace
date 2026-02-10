package com.tontin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("checkin_target")
public class CheckinTarget {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String name;
    private String description;
    private String targetType;
    private String color;
    private Integer dailyCount;
    private Date startTime;
    private Date endTime;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
