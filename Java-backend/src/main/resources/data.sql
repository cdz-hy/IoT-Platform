-- 初始化产品数据
INSERT IGNORE INTO product (id, name, type, description, created_time, updated_time) VALUES
('P_TEMP_SENSOR', '温湿度传感器', 'sensor', 'DHT11温湿度传感器', NOW(), NOW()),
('P_SMART_LIGHT', '智能LED灯', 'actuator', '可调亮度LED灯', NOW(), NOW()),
('P_SMOKE_ALARM', '烟雾报警器', 'sensor', 'MQ-2烟雾传感器', NOW(), NOW());

-- 初始化物模型属性
INSERT IGNORE INTO thing_property (id, product_id, name, identifier, data_type, min_value, max_value, unit, access_mode, created_time, updated_time) VALUES
('PROP_TEMP', 'P_TEMP_SENSOR', '温度', 'temperature', 'double', '-20', '50', '℃', 'read', NOW(), NOW()),
('PROP_HUMIDITY', 'P_TEMP_SENSOR', '湿度', 'humidity', 'int', '0', '100', '%', 'read', NOW(), NOW()),
('PROP_SWITCH', 'P_SMART_LIGHT', '开关', 'switch', 'boolean', NULL, NULL, NULL, 'read_write', NOW(), NOW()),
('PROP_BRIGHTNESS', 'P_SMART_LIGHT', '亮度', 'brightness', 'int', '0', '100', '%', 'read_write', NOW(), NOW()),
('PROP_SMOKE', 'P_SMOKE_ALARM', '烟雾浓度', 'smoke_level', 'double', '0', '1000', 'ppm', 'read', NOW(), NOW()),
('PROP_ALARM', 'P_SMOKE_ALARM', '报警状态', 'alarm', 'boolean', NULL, NULL, NULL, 'read', NOW(), NOW());

-- 初始化物模型服务
INSERT IGNORE INTO thing_service (id, product_id, name, identifier, input_params, output_params, description, created_time, updated_time) VALUES
('SVC_CALIBRATE', 'P_TEMP_SENSOR', '校准传感器', 'calibrate', '[{"name":"偏移量","identifier":"offset","dataType":"double"}]', '[{"name":"执行结果","identifier":"success","dataType":"boolean"}]', '校准传感器偏移量', NOW(), NOW()),
('SVC_SET_SWITCH', 'P_SMART_LIGHT', '开关控制', 'setSwitch', '[{"name":"开关","identifier":"switch","dataType":"boolean"}]', '[{"name":"执行结果","identifier":"success","dataType":"boolean"}]', '控制智能灯开关', NOW(), NOW()),
('SVC_SET_BRIGHTNESS', 'P_SMART_LIGHT', '设置亮度', 'setBrightness', '[{"name":"亮度","identifier":"brightness","dataType":"int","min":0,"max":100}]', '[{"name":"执行结果","identifier":"success","dataType":"boolean"}]', '设置智能灯亮度', NOW(), NOW());

-- 初始化物模型事件
INSERT IGNORE INTO thing_event (id, product_id, name, identifier, event_type, description, created_time, updated_time) VALUES
('EVT_HIGH_TEMP', 'P_TEMP_SENSOR', '高温事件', 'highTemperature', 'warn', '温度超过阈值时触发', NOW(), NOW()),
('EVT_LOW_BATTERY', 'P_TEMP_SENSOR', '低电量事件', 'lowBattery', 'warn', '设备电量低时触发', NOW(), NOW()),
('EVT_SMOKE', 'P_SMOKE_ALARM', '烟雾告警', 'smokeAlarm', 'error', '检测到烟雾浓度超标', NOW(), NOW());

-- 初始化告警规则
INSERT IGNORE INTO alert_rule (id, product_id, property_identifier, operator, threshold, alert_content, enabled, created_time, updated_time) VALUES
('RULE_HIGH_TEMP', 'P_TEMP_SENSOR', 'temperature', '>', '35', '温度过高告警：当前温度超过35℃', true, NOW(), NOW()),
('RULE_LOW_TEMP', 'P_TEMP_SENSOR', 'temperature', '<', '-10', '温度过低告警：当前温度低于-10℃', true, NOW(), NOW()),
('RULE_HIGH_HUMIDITY', 'P_TEMP_SENSOR', 'humidity', '>', '90', '湿度过高告警：当前湿度超过90%', true, NOW(), NOW()),
('RULE_SMOKE', 'P_SMOKE_ALARM', 'smoke_level', '>', '200', '烟雾浓度超标告警', true, NOW(), NOW());

-- 初始化设备
INSERT IGNORE INTO device (device_id, product_id, node_id, device_name, location, secret, status, created_time, updated_time) VALUES
('DVC_TEMP_001', 'P_TEMP_SENSOR', 'TEMP-MOCK-001', '演示温湿度设备', '教学楼A区', 'temp-secret-001', 'offline', NOW(), NOW()),
('DVC_LIGHT_001', 'P_SMART_LIGHT', 'LIGHT-MOCK-001', '演示智能灯设备', '实验室B区', 'light-secret-001', 'offline', NOW(), NOW());

-- 初始化设备影子
INSERT IGNORE INTO device_shadow (device_id, shadow_data, version, updated_time) VALUES
('DVC_TEMP_001', '{"version":1,"state":{"reported":{},"desired":{}},"delta":{}}', 1, NOW()),
('DVC_LIGHT_001', '{"version":1,"state":{"reported":{"switch":false,"brightness":0},"desired":{}},"delta":{}}', 1, NOW());

-- 初始化遥测数据
INSERT IGNORE INTO telemetry_latest (device_id, product_id, payload, reported_time) VALUES
('DVC_TEMP_001', 'P_TEMP_SENSOR', '{"temperature":24.2,"humidity":59}', NOW()),
('DVC_LIGHT_001', 'P_SMART_LIGHT', '{"switch":true,"brightness":80}', NOW());
