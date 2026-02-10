package com.tontin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    private Integer gender;
    private Date birthday;
    private String bio;
    private Integer score;
    private Integer memberLevel;
    private Date memberExpireTime;
    private Date lastLoginTime;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}