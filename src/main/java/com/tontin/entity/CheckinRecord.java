package com.tontin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("checkin_record")
public class CheckinRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long targetId;
    private Date checkinTime;
    private Integer checkinType;
    private Integer duration;
    private Double latitude;
    private Double longitude;
    private String address;
    private String remark;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
