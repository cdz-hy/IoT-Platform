-- H2数据库兼容的建表脚本

-- 产品表
CREATE TABLE IF NOT EXISTS product (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(30),
    created_time TIMESTAMP
);

-- 物模型属性表
CREATE TABLE IF NOT EXISTS thing_property (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    property_name VARCHAR(50),
    data_type VARCHAR(20),
    min_value VARCHAR(50),
    max_value VARCHAR(50),
    unit VARCHAR(20),
    access_mode VARCHAR(10),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- 物模型服务表
CREATE TABLE IF NOT EXISTS thing_service (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    service_name VARCHAR(50),
    input_params TEXT,
    output_params TEXT,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- 物模型事件表
CREATE TABLE IF NOT EXISTS thing_event (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    event_name VARCHAR(50),
    event_type VARCHAR(30),
    description TEXT,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- 设备表
CREATE TABLE IF NOT EXISTS device (
    device_id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    node_id VARCHAR(64) UNIQUE NOT NULL,
    device_name VARCHAR(50),
    secret VARCHAR(128),
    status VARCHAR(10) DEFAULT 'offline',
    last_online_time TIMESTAMP,
    created_time TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- 设备影子表
CREATE TABLE IF NOT EXISTS device_shadow (
    device_id VARCHAR(64) PRIMARY KEY,
    shadow_data CLOB,
    version BIGINT DEFAULT 1,
    updated_time TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);

-- 设备上下线记录表
CREATE TABLE IF NOT EXISTS device_connect_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64),
    event_type VARCHAR(10),
    event_time TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);

-- 命令记录表
CREATE TABLE IF NOT EXISTS command_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64),
    command_id VARCHAR(64),
    command_name VARCHAR(50),
    command_params TEXT,
    status VARCHAR(20) DEFAULT 'pending',
    result TEXT,
    request_time TIMESTAMP,
    response_time TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);

-- 告警规则表
CREATE TABLE IF NOT EXISTS alert_rule (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64),
    property_name VARCHAR(50),
    condition_expr VARCHAR(255),
    alert_content TEXT,
    enabled BOOLEAN DEFAULT TRUE
);

-- 告警记录表
CREATE TABLE IF NOT EXISTS alert_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64),
    rule_id VARCHAR(64),
    alert_content TEXT,
    alert_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pending',
    confirmed_time TIMESTAMP,
    remark VARCHAR(255)
);
