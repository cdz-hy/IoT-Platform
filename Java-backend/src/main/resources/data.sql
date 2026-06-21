-- 初始化产品数据（使用INSERT IGNORE避免重复插入）
INSERT IGNORE INTO product (id, name, type, created_time) VALUES
('PROD_001', '温湿度传感器', '传感器', NOW()),
('PROD_002', '智能LED灯', '照明设备', NOW()),
('PROD_003', '烟雾报警器', '安防设备', NOW());

-- 初始化物模型属性
INSERT IGNORE INTO thing_property (id, product_id, property_name, data_type, min_value, max_value, unit, access_mode) VALUES
('PROP_001', 'PROD_001', 'temperature', 'double', '-20', '50', '℃', 'read'),
('PROP_002', 'PROD_001', 'humidity', 'int', '0', '100', '%', 'read'),
('PROP_003', 'PROD_002', 'switch', 'boolean', NULL, NULL, NULL, 'read_write'),
('PROP_004', 'PROD_002', 'brightness', 'int', '0', '100', '%', 'read_write'),
('PROP_005', 'PROD_003', 'smoke_level', 'double', '0', '1000', 'ppm', 'read'),
('PROP_006', 'PROD_003', 'alarm', 'boolean', NULL, NULL, NULL, 'read');

-- 初始化物模型服务
INSERT IGNORE INTO thing_service (id, product_id, service_name, input_params, output_params) VALUES
('SERV_001', 'PROD_001', '校准传感器', '[{"name":"offset","type":"double"}]', '[{"name":"result","type":"boolean"}]'),
('SERV_002', 'PROD_002', '调整亮度', '[{"name":"brightness","type":"int","min":0,"max":100}]', '[{"name":"result","type":"boolean"}]'),
('SERV_003', 'PROD_002', '重启设备', '[]', '[{"name":"result","type":"boolean"}]');

-- 初始化物模型事件
INSERT IGNORE INTO thing_event (id, product_id, event_name, event_type, description) VALUES
('EVT_001', 'PROD_001', '高温告警', 'alert', '温度超过阈值时触发'),
('EVT_002', 'PROD_001', '低电量告警', 'alert', '设备电量低时触发'),
('EVT_003', 'PROD_003', '烟雾告警', 'alert', '检测到烟雾浓度超标');

-- 初始化告警规则
INSERT IGNORE INTO alert_rule (id, product_id, property_name, condition_expr, alert_content, enabled) VALUES
('RULE_001', 'PROD_001', 'temperature', '> 35', '温度过高告警：当前温度超过35℃', true),
('RULE_002', 'PROD_001', 'temperature', '< -10', '温度过低告警：当前温度低于-10℃', true),
('RULE_003', 'PROD_001', 'humidity', '> 90', '湿度过高告警：当前湿度超过90%', true),
('RULE_004', 'PROD_003', 'smoke_level', '> 200', '烟雾浓度超标告警', true);
