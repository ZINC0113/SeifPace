package com.tontin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;

@Data
@TableName("member_order")
public class MemberOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String orderNo;
    private Integer memberType;
    private BigDecimal amount;
    private Integer payType;
    private Date payTime;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
