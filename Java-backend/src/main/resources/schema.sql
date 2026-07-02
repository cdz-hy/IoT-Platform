-- IoT Platform 数据库初始化脚本 (MySQL)

CREATE DATABASE IF NOT EXISTS iot_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE iot_platform;

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    nickname VARCHAR(50) NULL COMMENT '显示名称',
    role VARCHAR(30) NOT NULL DEFAULT 'admin' COMMENT '角色',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    last_login_time DATETIME NULL COMMENT '最后登录时间',
    created_time DATETIME NULL COMMENT '创建时间',
    updated_time DATETIME NULL COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 产品表
CREATE TABLE IF NOT EXISTS product (
    id VARCHAR(64) PRIMARY KEY COMMENT '产品ID',
    name VARCHAR(50) NOT NULL COMMENT '产品名称',
    type VARCHAR(30) NOT NULL COMMENT '产品类型',
    description VARCHAR(255) NULL COMMENT '产品描述',
    created_time DATETIME NULL COMMENT '创建时间',
    updated_time DATETIME NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 物模型属性表
CREATE TABLE IF NOT EXISTS thing_property (
    id VARCHAR(64) PRIMARY KEY COMMENT '属性ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    name VARCHAR(50) NOT NULL COMMENT '属性名称',
    identifier VARCHAR(50) NOT NULL COMMENT '属性标识符',
    data_type VARCHAR(20) NOT NULL COMMENT '数据类型: int/double/boolean/string',
    min_value VARCHAR(50) NULL COMMENT '最小值',
    max_value VARCHAR(50) NULL COMMENT '最大值',
    unit VARCHAR(20) NULL COMMENT '单位',
    access_mode VARCHAR(20) NOT NULL COMMENT '访问权限: read/read_write',
    created_time DATETIME NULL,
    updated_time DATETIME NULL,
    UNIQUE KEY uk_property_identifier (product_id, identifier),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物模型属性表';

-- 物模型服务表
CREATE TABLE IF NOT EXISTS thing_service (
    id VARCHAR(64) PRIMARY KEY COMMENT '服务ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    name VARCHAR(50) NOT NULL COMMENT '服务名称',
    identifier VARCHAR(50) NOT NULL COMMENT '服务标识符',
    input_params TEXT NULL COMMENT '输入参数JSON',
    output_params TEXT NULL COMMENT '输出参数JSON',
    description VARCHAR(255) NULL COMMENT '服务描述',
    created_time DATETIME NULL,
    updated_time DATETIME NULL,
    UNIQUE KEY uk_service_identifier (product_id, identifier),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物模型服务表';

-- 物模型事件表
CREATE TABLE IF NOT EXISTS thing_event (
    id VARCHAR(64) PRIMARY KEY COMMENT '事件ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    name VARCHAR(50) NOT NULL COMMENT '事件名称',
    identifier VARCHAR(50) NOT NULL COMMENT '事件标识符',
    event_type VARCHAR(30) NOT NULL COMMENT '事件类型: info/warn/error',
    description VARCHAR(255) NULL COMMENT '事件描述',
    created_time DATETIME NULL,
    updated_time DATETIME NULL,
    UNIQUE KEY uk_event_identifier (product_id, identifier),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物模型事件表';

-- 设备表
CREATE TABLE IF NOT EXISTS device (
    device_id VARCHAR(64) PRIMARY KEY COMMENT '设备ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    node_id VARCHAR(64) NOT NULL COMMENT '设备物理标识',
    device_name VARCHAR(50) NOT NULL COMMENT '设备名称',
    location VARCHAR(100) NULL COMMENT '设备位置',
    secret VARCHAR(128) NOT NULL COMMENT 'MQTT连接密钥',
    status VARCHAR(20) NOT NULL DEFAULT 'offline' COMMENT '在线状态',
    last_online_time DATETIME NULL COMMENT '最后上线时间',
    activated_time DATETIME NULL COMMENT '首次激活时间',
    created_time DATETIME NULL COMMENT '注册时间',
    updated_time DATETIME NULL COMMENT '更新时间',
    UNIQUE KEY uk_node_id (node_id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- 设备影子表
CREATE TABLE IF NOT EXISTS device_shadow (
    device_id VARCHAR(64) PRIMARY KEY COMMENT '设备ID',
    shadow_data JSON NOT NULL COMMENT '影子JSON',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    updated_time DATETIME NULL COMMENT '更新时间',
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备影子表';

-- 设备影子变更日志表
CREATE TABLE IF NOT EXISTS device_shadow_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    device_id VARCHAR(64) NOT NULL COMMENT '设备ID',
    version INT NOT NULL COMMENT '变更后版本号',
    change_type VARCHAR(30) NOT NULL COMMENT '变更类型: reported/desired/delta',
    before_state JSON NULL COMMENT '变更前状态',
    after_state JSON NULL COMMENT '变更后状态',
    created_time DATETIME NULL COMMENT '变更时间',
    INDEX idx_device_time (device_id, created_time),
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备影子变更日志表';

-- 设备最新遥测数据表
CREATE TABLE IF NOT EXISTS telemetry_latest (
    device_id VARCHAR(64) PRIMARY KEY COMMENT '设备ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    payload JSON NOT NULL COMMENT '最新属性JSON',
    reported_time DATETIME NOT NULL COMMENT '上报时间',
    INDEX idx_product_id (product_id),
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备最新遥测数据表';

-- 设备遥测历史记录表
CREATE TABLE IF NOT EXISTS telemetry_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    device_id VARCHAR(64) NOT NULL COMMENT '设备ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    payload JSON NOT NULL COMMENT '属性JSON',
    reported_time DATETIME NOT NULL COMMENT '上报时间',
    INDEX idx_device_time (device_id, reported_time),
    INDEX idx_product_time (product_id, reported_time),
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备遥测历史记录表';

-- 设备上下线记录表
CREATE TABLE IF NOT EXISTS device_connect_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    device_id VARCHAR(64) NOT NULL COMMENT '设备ID',
    event_type VARCHAR(20) NOT NULL COMMENT '事件类型: online/offline/auth_failed',
    event_time DATETIME NOT NULL COMMENT '事件时间',
    client_id VARCHAR(128) NULL COMMENT 'MQTT ClientID',
    reason VARCHAR(255) NULL COMMENT '原因说明',
    INDEX idx_device_time (device_id, event_time),
    INDEX idx_event_time (event_time),
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备上下线记录表';

-- 命令记录表
CREATE TABLE IF NOT EXISTS command_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    command_id VARCHAR(64) NOT NULL COMMENT '命令业务ID',
    device_id VARCHAR(64) NOT NULL COMMENT '设备ID',
    service_identifier VARCHAR(50) NULL COMMENT '服务标识符',
    command_name VARCHAR(50) NOT NULL COMMENT '命令名称',
    command_params JSON NULL COMMENT '命令参数',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/success/failed',
    result JSON NULL COMMENT '执行结果',
    request_time DATETIME NOT NULL COMMENT '请求时间',
    response_time DATETIME NULL COMMENT '响应时间',
    UNIQUE KEY uk_command_id (command_id),
    INDEX idx_device_time (device_id, request_time),
    INDEX idx_status (status),
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='命令记录表';

-- 告警规则表
CREATE TABLE IF NOT EXISTS alert_rule (
    id VARCHAR(64) PRIMARY KEY COMMENT '规则ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    property_identifier VARCHAR(50) NOT NULL COMMENT '属性标识符',
    operator VARCHAR(10) NOT NULL COMMENT '运算符: >, >=, <, <=, ==, !=',
    threshold VARCHAR(50) NOT NULL COMMENT '阈值',
    alert_content VARCHAR(255) NOT NULL COMMENT '告警内容',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    created_time DATETIME NULL,
    updated_time DATETIME NULL,
    INDEX idx_product_id (product_id),
    INDEX idx_enabled (enabled),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则表';

-- 告警记录表
CREATE TABLE IF NOT EXISTS alert_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '告警ID',
    device_id VARCHAR(64) NOT NULL COMMENT '设备ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    rule_id VARCHAR(64) NULL COMMENT '规则ID',
    property_identifier VARCHAR(50) NOT NULL COMMENT '触发属性',
    actual_value VARCHAR(50) NOT NULL COMMENT '实际值',
    alert_content VARCHAR(255) NOT NULL COMMENT '告警内容',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/confirmed',
    alert_time DATETIME NOT NULL COMMENT '告警时间',
    confirmed_time DATETIME NULL COMMENT '确认时间',
    remark VARCHAR(255) NULL COMMENT '备注',
    INDEX idx_device_time (device_id, alert_time),
    INDEX idx_status (status),
    INDEX idx_alert_time (alert_time),
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE,
    FOREIGN KEY (rule_id) REFERENCES alert_rule(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

-- 设备事件日志表
CREATE TABLE IF NOT EXISTS device_event_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '事件ID',
    device_id VARCHAR(64) NOT NULL COMMENT '设备ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    event_identifier VARCHAR(50) NOT NULL COMMENT '事件标识符',
    event_type VARCHAR(30) NOT NULL COMMENT '事件类型: info/warn/error',
    payload JSON NULL COMMENT '事件内容',
    event_time DATETIME NOT NULL COMMENT '事件时间',
    INDEX idx_device_time (device_id, event_time),
    INDEX idx_product_time (product_id, event_time),
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备事件日志表';
