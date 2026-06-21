#!/usr/bin/env python3
"""
IoT设备模拟器 - 用于测试MQTT通信
使用前请安装: pip install paho-mqtt
"""

import json
import time
import random
import sys
from datetime import datetime

try:
    import paho.mqtt.client as mqtt
except ImportError:
    print("请先安装 paho-mqtt: pip install paho-mqtt")
    sys.exit(1)


class DeviceSimulator:
    """设备模拟器"""

    def __init__(self, broker_host="localhost", broker_port=1883):
        self.broker_host = broker_host
        self.broker_port = broker_port
        self.client = None
        self.device_id = None
        self.product_id = None
        self.connected = False

    def connect(self, device_id, product_id, secret=None):
        """连接到MQTT Broker"""
        self.device_id = device_id
        self.product_id = product_id

        # 创建客户端
        self.client = mqtt.Client(client_id=device_id)

        # 设置认证（如果有密钥）
        if secret:
            self.client.username_pw_set(device_id, secret)

        # 设置回调
        self.client.on_connect = self._on_connect
        self.client.on_disconnect = self._on_disconnect
        self.client.on_message = self._on_message

        # 连接
        try:
            self.client.connect(self.broker_host, self.broker_port, 60)
            self.client.loop_start()
            print(f"[{self._now()}] 正在连接到 {self.broker_host}:{self.broker_port}...")
            time.sleep(1)
            return True
        except Exception as e:
            print(f"[{self._now()}] 连接失败: {e}")
            return False

    def disconnect(self):
        """断开连接"""
        if self.client:
            self.client.loop_stop()
            self.client.disconnect()
            self.connected = False
            print(f"[{self._now()}] 已断开连接")

    def publish_telemetry(self, data):
        """上报属性数据"""
        topic = f"iot/{self.product_id}/{self.device_id}/telemetry"
        payload = json.dumps(data, ensure_ascii=False)
        self.client.publish(topic, payload, qos=1)
        print(f"[{self._now()}] 上报数据 -> {topic}")
        print(f"  数据: {payload}")

    def publish_event(self, event_name, event_data):
        """上报事件"""
        topic = f"iot/{self.product_id}/{self.device_id}/event"
        payload = json.dumps({
            "eventName": event_name,
            "data": event_data,
            "timestamp": int(time.time() * 1000)
        }, ensure_ascii=False)
        self.client.publish(topic, payload, qos=1)
        print(f"[{self._now()}] 上报事件 -> {topic}")
        print(f"  事件: {event_name}")

    def _on_connect(self, client, userdata, flags, rc):
        """连接成功回调"""
        if rc == 0:
            self.connected = True
            print(f"[{self._now()}] 连接成功!")

            # 订阅命令主题
            command_topic = f"iot/{self.product_id}/{self.device_id}/command/#"
            client.subscribe(command_topic)
            print(f"[{self._now()}] 订阅主题: {command_topic}")
        else:
            print(f"[{self._now()}] 连接失败，返回码: {rc}")

    def _on_disconnect(self, client, userdata, rc):
        """断开连接回调"""
        self.connected = False
        print(f"[{self._now()}] 连接断开")

    def _on_message(self, client, userdata, msg):
        """收到消息回调"""
        topic = msg.topic
        payload = msg.payload.decode("utf-8")
        print(f"[{self._now()}] 收到消息 <- {topic}")
        print(f"  内容: {payload}")

        # 处理命令
        if "/command/" in topic:
            self._handle_command(topic, payload)

    def _handle_command(self, topic, payload):
        """处理平台下发的命令"""
        try:
            # 从主题中提取命令ID
            parts = topic.split("/")
            command_id = parts[-1] if len(parts) > 4 else "unknown"

            # 解析命令
            command = json.loads(payload)
            print(f"[{self._now()}] 执行命令: {command}")

            # 模拟执行命令
            time.sleep(0.5)

            # 发送命令响应
            response_topic = f"iot/{self.product_id}/{self.device_id}/command/response"
            response = json.dumps({
                "commandId": command_id,
                "status": "success",
                "result": "命令执行成功"
            }, ensure_ascii=False)
            self.client.publish(response_topic, response, qos=1)
            print(f"[{self._now()}] 命令响应已发送")

        except Exception as e:
            print(f"[{self._now()}] 命令处理失败: {e}")

    def _now(self):
        """获取当前时间字符串"""
        return datetime.now().strftime("%H:%M:%S")


def simulate_temperature_sensor():
    """模拟温湿度传感器"""
    print("=" * 50)
    print("IoT设备模拟器 - 温湿度传感器")
    print("=" * 50)

    simulator = DeviceSimulator()

    # 输入设备信息
    device_id = input("请输入设备ID (如 DEV_xxxxxxxx): ").strip()
    product_id = input("请输入产品ID (如 PROD_001): ").strip()

    if not device_id or not product_id:
        print("设备ID和产品ID不能为空")
        return

    # 连接
    if not simulator.connect(device_id, product_id):
        return

    print("\n开始模拟数据上报...")
    print("按 Ctrl+C 停止\n")

    try:
        while True:
            # 生成随机温湿度数据
            temperature = round(random.uniform(20, 35), 1)
            humidity = random.randint(40, 80)

            # 上报数据
            simulator.publish_telemetry({
                "temperature": temperature,
                "humidity": humidity
            })

            # 随机触发事件
            if temperature > 32:
                simulator.publish_event("高温告警", {
                    "temperature": temperature,
                    "threshold": 32
                })

            print()
            time.sleep(5)  # 每5秒上报一次

    except KeyboardInterrupt:
        print("\n停止模拟...")
    finally:
        simulator.disconnect()


def simulate_smart_light():
    """模拟智能灯"""
    print("=" * 50)
    print("IoT设备模拟器 - 智能LED灯")
    print("=" * 50)

    simulator = DeviceSimulator()

    device_id = input("请输入设备ID: ").strip()
    product_id = input("请输入产品ID (如 PROD_002): ").strip()

    if not simulator.connect(device_id, product_id):
        return

    print("\n开始模拟...")
    print("按 Ctrl+C 停止\n")

    try:
        switch_state = True
        brightness = 80

        while True:
            simulator.publish_telemetry({
                "switch": switch_state,
                "brightness": brightness
            })

            print()
            time.sleep(10)

    except KeyboardInterrupt:
        print("\n停止模拟...")
    finally:
        simulator.disconnect()


if __name__ == "__main__":
    print("\nIoT设备模拟器")
    print("1. 温湿度传感器")
    print("2. 智能LED灯")

    choice = input("\n请选择设备类型 (1/2): ").strip()

    if choice == "1":
        simulate_temperature_sensor()
    elif choice == "2":
        simulate_smart_light()
    else:
        print("无效选择")
