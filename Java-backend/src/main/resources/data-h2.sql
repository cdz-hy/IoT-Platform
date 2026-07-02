-- H2兼容的初始化数据脚本

-- 管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO sys_user (username, password, nickname, role, enabled, created_time, updated_time)
SELECT 'admin', '$2b$10$w4URFEjbIXsqLKpaFzQpKe0gZIc9VtG7I.QTr6tt8iujjUaXIKL0.', '平台管理员', 'admin', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

-- 产品数据
INSERT INTO product (id, name, type, description, created_time, updated_time)
SELECT 'P_TEMP_SENSOR', '温湿度传感器', 'sensor', '用于上报温度和湿度数据的传感器产品', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 'P_TEMP_SENSOR');

INSERT INTO product (id, name, type, description, created_time, updated_time)
SELECT 'P_SMART_LIGHT', '智能LED灯', 'actuator', '支持开关和亮度控制的智能灯产品', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 'P_SMART_LIGHT');

INSERT INTO product (id, name, type, description, created_time, updated_time)
SELECT 'P_SMOKE_ALARM', '烟雾报警器', 'sensor', '检测烟雾浓度的安防设备', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 'P_SMOKE_ALARM');

-- 物模型属性
INSERT INTO thing_property (id, product_id, name, identifier, data_type, min_value, max_value, unit, access_mode, created_time, updated_time)
SELECT 'PROP_TEMP', 'P_TEMP_SENSOR', '温度', 'temperature', 'double', '-20', '50', '℃', 'read', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_TEMP');

INSERT INTO thing_property (id, product_id, name, identifier, data_type, min_value, max_value, unit, access_mode, created_time, updated_time)
SELECT 'PROP_HUMIDITY', 'P_TEMP_SENSOR', '湿度', 'humidity', 'int', '0', '100', '%', 'read', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_HUMIDITY');

INSERT INTO thing_property (id, product_id, name, identifier, data_type, min_value, max_value, unit, access_mode, created_time, updated_time)
SELECT 'PROP_SWITCH', 'P_SMART_LIGHT', '开关', 'switch', 'boolean', NULL, NULL, NULL, 'read_write', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_SWITCH');

INSERT INTO thing_property (id, product_id, name, identifier, data_type, min_value, max_value, unit, access_mode, created_time, updated_time)
SELECT 'PROP_BRIGHTNESS', 'P_SMART_LIGHT', '亮度', 'brightness', 'int', '0', '100', '%', 'read_write', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_BRIGHTNESS');

INSERT INTO thing_property (id, product_id, name, identifier, data_type, min_value, max_value, unit, access_mode, created_time, updated_time)
SELECT 'PROP_SMOKE', 'P_SMOKE_ALARM', '烟雾浓度', 'smoke_level', 'double', '0', '1000', 'ppm', 'read', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_SMOKE');

INSERT INTO thing_property (id, product_id, name, identifier, data_type, min_value, max_value, unit, access_mode, created_time, updated_time)
SELECT 'PROP_ALARM', 'P_SMOKE_ALARM', '报警状态', 'alarm', 'boolean', NULL, NULL, NULL, 'read', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_ALARM');

-- 物模型服务
INSERT INTO thing_service (id, product_id, name, identifier, input_params, output_params, description, created_time, updated_time)
SELECT 'SVC_CALIBRATE', 'P_TEMP_SENSOR', '校准传感器', 'calibrate', '[{"name":"offset","identifier":"offset","dataType":"double"}]', '[{"name":"result","identifier":"success","dataType":"boolean"}]', '校准传感器偏移量', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_service WHERE id = 'SVC_CALIBRATE');

INSERT INTO thing_service (id, product_id, name, identifier, input_params, output_params, description, created_time, updated_time)
SELECT 'SVC_SET_SWITCH', 'P_SMART_LIGHT', '开关控制', 'setSwitch', '[{"name":"开关","identifier":"switch","dataType":"boolean"}]', '[{"name":"result","identifier":"success","dataType":"boolean"}]', '控制智能灯开关', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_service WHERE id = 'SVC_SET_SWITCH');

INSERT INTO thing_service (id, product_id, name, identifier, input_params, output_params, description, created_time, updated_time)
SELECT 'SVC_SET_BRIGHTNESS', 'P_SMART_LIGHT', '设置亮度', 'setBrightness', '[{"name":"亮度","identifier":"brightness","dataType":"int","min":0,"max":100}]', '[{"name":"result","identifier":"success","dataType":"boolean"}]', '设置智能灯亮度', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_service WHERE id = 'SVC_SET_BRIGHTNESS');

-- 物模型事件
INSERT INTO thing_event (id, product_id, name, identifier, event_type, description, created_time, updated_time)
SELECT 'EVT_HIGH_TEMP', 'P_TEMP_SENSOR', '高温事件', 'highTemperature', 'warn', '温度超过阈值时触发', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_event WHERE id = 'EVT_HIGH_TEMP');

INSERT INTO thing_event (id, product_id, name, identifier, event_type, description, created_time, updated_time)
SELECT 'EVT_LOW_BATTERY', 'P_TEMP_SENSOR', '低电量事件', 'lowBattery', 'warn', '设备电量低时触发', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_event WHERE id = 'EVT_LOW_BATTERY');

INSERT INTO thing_event (id, product_id, name, identifier, event_type, description, created_time, updated_time)
SELECT 'EVT_SMOKE', 'P_SMOKE_ALARM', '烟雾告警', 'smokeAlarm', 'error', '检测到烟雾浓度超标', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM thing_event WHERE id = 'EVT_SMOKE');

-- 设备
INSERT INTO device (device_id, product_id, node_id, device_name, location, secret, status, created_time, updated_time)
SELECT 'DVC_TEMP_001', 'P_TEMP_SENSOR', 'TEMP-MOCK-001', '演示温湿度设备', '教学楼A区', 'temp-secret-001', 'offline', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device WHERE device_id = 'DVC_TEMP_001');

INSERT INTO device (device_id, product_id, node_id, device_name, location, secret, status, created_time, updated_time)
SELECT 'DVC_LIGHT_001', 'P_SMART_LIGHT', 'LIGHT-MOCK-001', '演示智能灯设备', '实验室B区', 'light-secret-001', 'offline', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device WHERE device_id = 'DVC_LIGHT_001');

-- 设备影子
INSERT INTO device_shadow (device_id, shadow_data, version, updated_time)
SELECT 'DVC_TEMP_001', '{"version":1,"state":{"reported":{},"desired":{}},"delta":{}}', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device_shadow WHERE device_id = 'DVC_TEMP_001');

INSERT INTO device_shadow (device_id, shadow_data, version, updated_time)
SELECT 'DVC_LIGHT_001', '{"version":1,"state":{"reported":{"switch":false,"brightness":0},"desired":{}},"delta":{}}', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device_shadow WHERE device_id = 'DVC_LIGHT_001');

-- 告警规则
INSERT INTO alert_rule (id, product_id, property_identifier, operator, threshold, alert_content, enabled, created_time, updated_time)
SELECT 'RULE_HIGH_TEMP', 'P_TEMP_SENSOR', 'temperature', '>', '35', '温度过高告警：当前温度超过35℃', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM alert_rule WHERE id = 'RULE_HIGH_TEMP');

INSERT INTO alert_rule (id, product_id, property_identifier, operator, threshold, alert_content, enabled, created_time, updated_time)
SELECT 'RULE_LOW_TEMP', 'P_TEMP_SENSOR', 'temperature', '<', '-10', '温度过低告警：当前温度低于-10℃', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM alert_rule WHERE id = 'RULE_LOW_TEMP');

INSERT INTO alert_rule (id, product_id, property_identifier, operator, threshold, alert_content, enabled, created_time, updated_time)
SELECT 'RULE_HIGH_HUMIDITY', 'P_TEMP_SENSOR', 'humidity', '>', '90', '湿度过高告警：当前湿度超过90%', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM alert_rule WHERE id = 'RULE_HIGH_HUMIDITY');

INSERT INTO alert_rule (id, product_id, property_identifier, operator, threshold, alert_content, enabled, created_time, updated_time)
SELECT 'RULE_SMOKE', 'P_SMOKE_ALARM', 'smoke_level', '>', '200', '烟雾浓度超标告警', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM alert_rule WHERE id = 'RULE_SMOKE');

-- 初始遥测数据
INSERT INTO telemetry_latest (device_id, product_id, payload, reported_time)
SELECT 'DVC_TEMP_001', 'P_TEMP_SENSOR', '{"temperature":24.2,"humidity":59}', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM telemetry_latest WHERE device_id = 'DVC_TEMP_001');

INSERT INTO telemetry_latest (device_id, product_id, payload, reported_time)
SELECT 'DVC_LIGHT_001', 'P_SMART_LIGHT', '{"switch":true,"brightness":80}', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM telemetry_latest WHERE device_id = 'DVC_LIGHT_001');
