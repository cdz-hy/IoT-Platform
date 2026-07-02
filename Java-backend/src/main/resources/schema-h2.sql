-- H2数据库兼容的建表脚本

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50) NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'admin',
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    last_login_time TIMESTAMP NULL,
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(30) NOT NULL,
    description VARCHAR(255) NULL,
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS thing_property (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    name VARCHAR(50) NOT NULL,
    identifier VARCHAR(50) NOT NULL,
    data_type VARCHAR(20) NOT NULL,
    min_value VARCHAR(50) NULL,
    max_value VARCHAR(50) NULL,
    unit VARCHAR(20) NULL,
    access_mode VARCHAR(20) NOT NULL,
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS thing_service (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    name VARCHAR(50) NOT NULL,
    identifier VARCHAR(50) NOT NULL,
    input_params TEXT NULL,
    output_params TEXT NULL,
    description VARCHAR(255) NULL,
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS thing_event (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    name VARCHAR(50) NOT NULL,
    identifier VARCHAR(50) NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    description VARCHAR(255) NULL,
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS device (
    device_id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    node_id VARCHAR(64) NOT NULL,
    device_name VARCHAR(50) NOT NULL,
    location VARCHAR(100) NULL,
    secret VARCHAR(128) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'offline',
    last_online_time TIMESTAMP NULL,
    activated_time TIMESTAMP NULL,
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS device_shadow (
    device_id VARCHAR(64) PRIMARY KEY,
    shadow_data CLOB NOT NULL,
    version INT NOT NULL DEFAULT 1,
    updated_time TIMESTAMP NOT NULL,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS device_shadow_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64) NOT NULL,
    version INT NOT NULL,
    change_type VARCHAR(30) NOT NULL,
    before_state CLOB NULL,
    after_state CLOB NULL,
    created_time TIMESTAMP NOT NULL,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS telemetry_latest (
    device_id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    payload CLOB NOT NULL,
    reported_time TIMESTAMP NOT NULL,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS telemetry_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64) NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    payload CLOB NOT NULL,
    reported_time TIMESTAMP NOT NULL,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS device_connect_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    event_time TIMESTAMP NOT NULL,
    client_id VARCHAR(128) NULL,
    reason VARCHAR(255) NULL,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS command_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    command_id VARCHAR(64) NOT NULL,
    device_id VARCHAR(64) NOT NULL,
    service_identifier VARCHAR(50) NULL,
    command_name VARCHAR(50) NOT NULL,
    command_params CLOB NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    result CLOB NULL,
    request_time TIMESTAMP NOT NULL,
    response_time TIMESTAMP NULL,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS alert_rule (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    property_identifier VARCHAR(50) NOT NULL,
    operator VARCHAR(10) NOT NULL,
    threshold VARCHAR(50) NOT NULL,
    alert_content VARCHAR(255) NOT NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS alert_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64) NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    rule_id VARCHAR(64) NULL,
    property_identifier VARCHAR(50) NOT NULL,
    actual_value VARCHAR(50) NOT NULL,
    alert_content VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    alert_time TIMESTAMP NOT NULL,
    confirmed_time TIMESTAMP NULL,
    remark VARCHAR(255) NULL,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE,
    FOREIGN KEY (rule_id) REFERENCES alert_rule(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS device_event_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64) NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    event_identifier VARCHAR(50) NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    payload CLOB NULL,
    event_time TIMESTAMP NOT NULL,
    FOREIGN KEY (device_id) REFERENCES device(device_id) ON DELETE CASCADE
);
