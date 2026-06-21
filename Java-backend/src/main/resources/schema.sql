-- IoT Platform 数据库初始化脚本 (MySQL)
-- 支持重复执行，使用 IF NOT EXISTS

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS iot_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE iot_platform;

-- 产品表
CREATE TABLE IF NOT EXISTS product (
    id VARCHAR(64) PRIMARY KEY COMMENT '产品ID',
    name VARCHAR(50) NOT NULL COMMENT '产品名称',
    type VARCHAR(30) COMMENT '产品类型',
    created_time DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 物模型属性表
CREATE TABLE IF NOT EXISTS thing_property (
    id VARCHAR(64) PRIMARY KEY COMMENT '属性ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    property_name VARCHAR(50) COMMENT '属性名称',
    data_type VARCHAR(20) COMMENT '数据类型: int, double, boolean, string',
    min_value VARCHAR(50) COMMENT '最小值',
    max_value VARCHAR(50) COMMENT '最大值',
    unit VARCHAR(20) COMMENT '单位',
    access_mode VARCHAR(10) COMMENT '访问权限: read, write, read_write',
    INDEX idx_product_id (product_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物模型属性表';

-- 物模型服务表
CREATE TABLE IF NOT EXISTS thing_service (
    id VARCHAR(64) PRIMARY KEY COMMENT '服务ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    service_name VARCHAR(50) COMMENT '服务名称',
    input_params TEXT COMMENT '输入参数(JSON)',
    output_params TEXT COMMENT '输出参数(JSON)',
    INDEX idx_product_id (product_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物模型服务表';

-- 物模型事件表
CREATE TABLE IF NOT EXISTS thing_event (
    id VARCHAR(64) PRIMARY KEY COMMENT '事件ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    event_name VARCHAR(50) COMMENT '事件名称',
    event_type VARCHAR(30) COMMENT '事件类型',
    description TEXT COMMENT '事件描述',
    INDEX idx_product_id (product_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物模型事件表';

-- 设备表
CREATE TABLE IF NOT EXISTS device (
    device_id VARCHAR(64) PRIMARY KEY COMMENT '设备ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    node_id VARCHAR(64) UNIQUE NOT NULL COMMENT '设备标识码(如IMEI/MAC)',
    device_name VARCHAR(50) COMMENT '设备名称',
    secret VARCHAR(128) COMMENT '连接密钥',
    status VARCHAR(10) DEFAULT 'offline' COMMENT '在线状态: online, offline',
    last_online_time DATETIME COMMENT '最后上线时间',
    created_time DATETIME COMMENT '注册时间',
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- 设备影子表
CREATE TABLE IF NOT EXISTS device_shadow (
    device_id VARCHAR(64) PRIMARY KEY COMMENT '设备ID',
    shadow_data JSON COMMENT '影子数据JSON(含reported/desired/delta)',
    version BIGINT DEFAULT 1 COMMENT '版本号(用于并发控制)',
    updated_time DATETIME COMMENT '更新时间',
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备影子表';

-- 设备上下线记录表
CREATE TABLE IF NOT EXISTS device_connect_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    device_id VARCHAR(64) COMMENT '设备ID',
    event_type VARCHAR(10) COMMENT '事件类型: online, offline',
    event_time DATETIME COMMENT '事件时间',
    INDEX idx_device_id (device_id),
    INDEX idx_event_time (event_time),
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备上下线记录表';

-- 命令记录表
CREATE TABLE IF NOT EXISTS command_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    device_id VARCHAR(64) COMMENT '设备ID',
    command_id VARCHAR(64) COMMENT '命令ID',
    command_name VARCHAR(50) COMMENT '命令名称',
    command_params TEXT COMMENT '命令参数(JSON)',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending, success, failed',
    result TEXT COMMENT '执行结果',
    request_time DATETIME COMMENT '请求时间',
    response_time DATETIME COMMENT '响应时间',
    INDEX idx_device_id (device_id),
    INDEX idx_command_id (command_id),
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='命令记录表';

-- 告警规则表
CREATE TABLE IF NOT EXISTS alert_rule (
    id VARCHAR(64) PRIMARY KEY COMMENT '规则ID',
    product_id VARCHAR(64) COMMENT '产品ID',
    property_name VARCHAR(50) COMMENT '属性名称',
    condition_expr VARCHAR(255) COMMENT '条件表达式(如 > 35)',
    alert_content TEXT COMMENT '告警内容',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则表';

-- 告警记录表
CREATE TABLE IF NOT EXISTS alert_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    device_id VARCHAR(64) COMMENT '设备ID',
    rule_id VARCHAR(64) COMMENT '规则ID',
    alert_content TEXT COMMENT '告警内容',
    alert_time DATETIME COMMENT '告警时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending, confirmed',
    confirmed_time DATETIME COMMENT '确认时间',
    remark VARCHAR(255) COMMENT '备注',
    INDEX idx_device_id (device_id),
    INDEX idx_alert_time (alert_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';
