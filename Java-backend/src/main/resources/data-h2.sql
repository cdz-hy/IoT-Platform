-- H2兼容的初始化数据脚本

-- 初始化产品数据
INSERT INTO product (id, name, type, created_time)
SELECT 'PROD_001', '温湿度传感器', '传感器', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 'PROD_001');

INSERT INTO product (id, name, type, created_time)
SELECT 'PROD_002', '智能LED灯', '照明设备', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 'PROD_002');

INSERT INTO product (id, name, type, created_time)
SELECT 'PROD_003', '烟雾报警器', '安防设备', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 'PROD_003');

-- 初始化物模型属性
INSERT INTO thing_property (id, product_id, property_name, data_type, min_value, max_value, unit, access_mode)
SELECT 'PROP_001', 'PROD_001', 'temperature', 'double', '-20', '50', '℃', 'read'
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_001');

INSERT INTO thing_property (id, product_id, property_name, data_type, min_value, max_value, unit, access_mode)
SELECT 'PROP_002', 'PROD_001', 'humidity', 'int', '0', '100', '%', 'read'
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_002');

INSERT INTO thing_property (id, product_id, property_name, data_type, min_value, max_value, unit, access_mode)
SELECT 'PROP_003', 'PROD_002', 'switch', 'boolean', NULL, NULL, NULL, 'read_write'
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_003');

INSERT INTO thing_property (id, product_id, property_name, data_type, min_value, max_value, unit, access_mode)
SELECT 'PROP_004', 'PROD_002', 'brightness', 'int', '0', '100', '%', 'read_write'
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_004');

INSERT INTO thing_property (id, product_id, property_name, data_type, min_value, max_value, unit, access_mode)
SELECT 'PROP_005', 'PROD_003', 'smoke_level', 'double', '0', '1000', 'ppm', 'read'
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_005');

INSERT INTO thing_property (id, product_id, property_name, data_type, min_value, max_value, unit, access_mode)
SELECT 'PROP_006', 'PROD_003', 'alarm', 'boolean', NULL, NULL, NULL, 'read'
WHERE NOT EXISTS (SELECT 1 FROM thing_property WHERE id = 'PROP_006');

-- 初始化物模型服务
INSERT INTO thing_service (id, product_id, service_name, input_params, output_params)
SELECT 'SERV_001', 'PROD_001', '校准传感器', '[{"name":"offset","type":"double"}]', '[{"name":"result","type":"boolean"}]'
WHERE NOT EXISTS (SELECT 1 FROM thing_service WHERE id = 'SERV_001');

INSERT INTO thing_service (id, product_id, service_name, input_params, output_params)
SELECT 'SERV_002', 'PROD_002', '调整亮度', '[{"name":"brightness","type":"int","min":0,"max":100}]', '[{"name":"result","type":"boolean"}]'
WHERE NOT EXISTS (SELECT 1 FROM thing_service WHERE id = 'SERV_002');

INSERT INTO thing_service (id, product_id, service_name, input_params, output_params)
SELECT 'SERV_003', 'PROD_002', '重启设备', '[]', '[{"name":"result","type":"boolean"}]'
WHERE NOT EXISTS (SELECT 1 FROM thing_service WHERE id = 'SERV_003');

-- 初始化物模型事件
INSERT INTO thing_event (id, product_id, event_name, event_type, description)
SELECT 'EVT_001', 'PROD_001', '高温告警', 'alert', '温度超过阈值时触发'
WHERE NOT EXISTS (SELECT 1 FROM thing_event WHERE id = 'EVT_001');

INSERT INTO thing_event (id, product_id, event_name, event_type, description)
SELECT 'EVT_002', 'PROD_001', '低电量告警', 'alert', '设备电量低时触发'
WHERE NOT EXISTS (SELECT 1 FROM thing_event WHERE id = 'EVT_002');

INSERT INTO thing_event (id, product_id, event_name, event_type, description)
SELECT 'EVT_003', 'PROD_003', '烟雾告警', 'alert', '检测到烟雾浓度超标'
WHERE NOT EXISTS (SELECT 1 FROM thing_event WHERE id = 'EVT_003');

-- 初始化告警规则
INSERT INTO alert_rule (id, product_id, property_name, condition_expr, alert_content, enabled)
SELECT 'RULE_001', 'PROD_001', 'temperature', '> 35', '温度过高告警：当前温度超过35℃', TRUE
WHERE NOT EXISTS (SELECT 1 FROM alert_rule WHERE id = 'RULE_001');

INSERT INTO alert_rule (id, product_id, property_name, condition_expr, alert_content, enabled)
SELECT 'RULE_002', 'PROD_001', 'temperature', '< -10', '温度过低告警：当前温度低于-10℃', TRUE
WHERE NOT EXISTS (SELECT 1 FROM alert_rule WHERE id = 'RULE_002');

INSERT INTO alert_rule (id, product_id, property_name, condition_expr, alert_content, enabled)
SELECT 'RULE_003', 'PROD_001', 'humidity', '> 90', '湿度过高告警：当前湿度超过90%', TRUE
WHERE NOT EXISTS (SELECT 1 FROM alert_rule WHERE id = 'RULE_003');

INSERT INTO alert_rule (id, product_id, property_name, condition_expr, alert_content, enabled)
SELECT 'RULE_004', 'PROD_003', 'smoke_level', '> 200', '烟雾浓度超标告警', TRUE
WHERE NOT EXISTS (SELECT 1 FROM alert_rule WHERE id = 'RULE_004');
