-- 创建数据库
CREATE DATABASE IF NOT EXISTS tontin_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tontin_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
    `password` VARCHAR(64) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(32) NOT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `bio` VARCHAR(255) DEFAULT NULL COMMENT '个人简介',
    `积分` INT DEFAULT 0 COMMENT '积分',
    `member_level` TINYINT DEFAULT 0 COMMENT '会员等级：0-普通，1-月度会员，2-季度会员，3-年度会员',
    `member_expire_time` DATETIME DEFAULT NULL COMMENT '会员到期时间',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 打卡目标表
CREATE TABLE IF NOT EXISTS `checkin_target` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '目标ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `name` VARCHAR(64) NOT NULL COMMENT '目标名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '目标描述',
    `target_type` VARCHAR(32) NOT NULL COMMENT '目标类型：吃饭，睡觉，运动等',
    `color` VARCHAR(16) DEFAULT '#409EFF' COMMENT '颜色',
    `daily_count` INT DEFAULT 1 COMMENT '每日打卡次数',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-进行中，0-已结束',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡目标表';

-- 打卡记录表
CREATE TABLE IF NOT EXISTS `checkin_record` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `target_id` BIGINT UNSIGNED NOT NULL COMMENT '目标ID',
    `checkin_time` DATETIME NOT NULL COMMENT '打卡时间',
    `checkin_type` TINYINT DEFAULT 1 COMMENT '打卡类型：1-手动打卡，2-定时打卡',
    `duration` INT DEFAULT 0 COMMENT '打卡时长（秒）',
    `latitude` DOUBLE DEFAULT NULL COMMENT '纬度',
    `longitude` DOUBLE DEFAULT NULL COMMENT '经度',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-异常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_target_id` (`target_id`),
    INDEX `idx_checkin_time` (`checkin_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS `chat_message` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `sender_id` BIGINT UNSIGNED NOT NULL COMMENT '发送者ID',
    `receiver_id` BIGINT UNSIGNED NOT NULL COMMENT '接收者ID（用户ID或组队ID）',
    `message_type` TINYINT NOT NULL COMMENT '消息类型：1-文本，2-表情，3-图片，4-视频，5-打卡提醒',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `team_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '组队ID，非空表示组队消息',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-已撤回',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_sender_id` (`sender_id`),
    INDEX `idx_receiver_id` (`receiver_id`),
    INDEX `idx_team_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 组队表
CREATE TABLE IF NOT EXISTS `team` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '组队ID',
    `name` VARCHAR(64) NOT NULL COMMENT '组队名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '组队描述',
    `creator_id` BIGINT UNSIGNED NOT NULL COMMENT '创建者ID',
    `max_member` INT DEFAULT 10 COMMENT '最大成员数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-活跃，0-解散',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组队表';

-- 组队成员表
CREATE TABLE IF NOT EXISTS `team_member` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `team_id` BIGINT UNSIGNED NOT NULL COMMENT '组队ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0-普通成员，1-管理员，2-创建者',
    `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-已退出',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_team_user` (`team_id`, `user_id`),
    INDEX `idx_team_id` (`team_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组队成员表';

-- 搭子表
CREATE TABLE IF NOT EXISTS `partner` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `partner_id` BIGINT UNSIGNED NOT NULL COMMENT '搭子用户ID',
    `match_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-活跃，0-已解除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_partner` (`user_id`, `partner_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_partner_id` (`partner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搭子表';

-- 会员订单表
CREATE TABLE IF NOT EXISTS `member_order` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `member_type` TINYINT NOT NULL COMMENT '会员类型：1-月度，2-季度，3-年度',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `pay_type` TINYINT DEFAULT NULL COMMENT '支付方式：1-微信，2-支付宝',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-待支付，2-已支付，3-已取消',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员订单表';

-- Flash表
CREATE TABLE IF NOT EXISTS `flash` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `type` TINYINT NOT NULL COMMENT '类型：1-照片，2-视频',
    `url` VARCHAR(255) NOT NULL COMMENT '文件URL',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `checkin_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联打卡ID',
    `message_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联消息ID',
    `visibility` TINYINT DEFAULT 1 COMMENT '可见性：1-仅自己，2-仅搭子，3-仅组队成员',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_checkin_id` (`checkin_id`),
    INDEX `idx_message_id` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Flash表';

-- 定时任务表（XXL-Job使用）
CREATE TABLE IF NOT EXISTS `xxl_job_info` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
    `job_desc` varchar(255) NOT NULL,
    `add_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `author` varchar(64) DEFAULT NULL COMMENT '作者',
    `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
    `schedule_type` varchar(50) NOT NULL DEFAULT 'CRON' COMMENT '调度类型',
    `schedule_conf` varchar(128) NOT NULL COMMENT '调度配置，CRON表达式',
    `misfire_strategy` varchar(50) NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
    `executor_route_strategy` varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
    `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
    `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
    `executor_timeout` int(11) NOT NULL DEFAULT 0 COMMENT '任务执行超时时间，单位秒',
    `executor_fail_retry_count` int(11) NOT NULL DEFAULT 0 COMMENT '失败重试次数',
    `glue_type` varchar(50) NOT NULL DEFAULT 'BEAN' COMMENT 'GLUE类型',
    `glue_source` mediumtext COMMENT 'GLUE源代码',
    `glue_remark` varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
    `glue_updatetime` datetime DEFAULT NULL COMMENT 'GLUE更新时间',
    `child_jobid` varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
    `trigger_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '调度状态：0-停止，1-运行',
    `trigger_last_time` bigint(13) NOT NULL DEFAULT 0 COMMENT '上次调度时间',
    `trigger_next_time` bigint(13) NOT NULL DEFAULT 0 COMMENT '下次调度时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='xxl_job_info';

-- 执行器表（XXL-Job使用）
CREATE TABLE IF NOT EXISTS `xxl_job_group` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
    `title` varchar(128) NOT NULL COMMENT '执行器名称',
    `address_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '执行器地址类型：0-自动注册，1-手动录入',
    `address_list` varchar(512) DEFAULT NULL COMMENT '执行器地址列表，多地址逗号分隔',
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_app_name` (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='xxl_job_group';

-- 触发器表（XXL-Job使用）
CREATE TABLE IF NOT EXISTS `xxl_job_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
    `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
    `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
    `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
    `executor_sharding_param` varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
    `executor_fail_retry_count` int(11) NOT NULL DEFAULT 0 COMMENT '失败重试次数',
    `trigger_time` datetime DEFAULT NULL COMMENT '调度-时间',
    `trigger_code` int(11) NOT NULL COMMENT '调度-结果',
    `trigger_msg` text COMMENT '调度-日志',
    `handle_time` datetime DEFAULT NULL COMMENT '执行-时间',
    `handle_code` int(11) NOT NULL COMMENT '执行-结果',
    `handle_msg` text COMMENT '执行-日志',
    `alarm_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
    PRIMARY KEY (`id`),
    KEY `i_trigger_time` (`trigger_time`),
    KEY `i_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='xxl_job_log';

-- 日志报告表（XXL-Job使用）
CREATE TABLE IF NOT EXISTS `xxl_job_log_report` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `trigger_day` datetime DEFAULT NULL COMMENT '调度-时间',
    `running_count` int(11) NOT NULL DEFAULT 0 COMMENT '运行中-数量',
    `suc_count` int(11) NOT NULL DEFAULT 0 COMMENT '执行成功-数量',
    `fail_count` int(11) NOT NULL DEFAULT 0 COMMENT '执行失败-数量',
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_trigger_day` (`trigger_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='xxl_job_log_report';

-- 日志详情表（XXL-Job使用）
CREATE TABLE IF NOT EXISTS `xxl_job_logglue` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
    `glue_type` varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
    `glue_source` mediumtext COMMENT 'GLUE源代码',
    `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
    `add_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `i_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='xxl_job_logglue';

-- 报警表（XXL-Job使用）
CREATE TABLE IF NOT EXISTS `xxl_job_registry` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `registry_group` varchar(50) NOT NULL,
    `registry_key` varchar(255) NOT NULL,
    `registry_value` varchar(255) NOT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='xxl_job_registry';

-- 用户表（XXL-Job使用）
CREATE TABLE IF NOT EXISTS `xxl_job_user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL COMMENT '账号',
    `password` varchar(50) NOT NULL COMMENT '密码',
    `role` tinyint(4) NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户、1-管理员',
    `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分隔',
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='xxl_job_user';