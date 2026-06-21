package com.iot.platform.service;

import com.iot.platform.entity.Device;
import com.iot.platform.entity.DeviceConnectLog;
import com.iot.platform.entity.CommandLog;

import java.util.List;

/**
 * 设备服务接口
 */
public interface DeviceService {

    /** 注册设备 */
    Device registerDevice(String productId, String nodeId, String deviceName);

    /** 更新设备 */
    Device updateDevice(String deviceId, Device device);

    /** 删除设备 */
    void deleteDevice(String deviceId);

    /** 根据ID查询设备 */
    Device getDeviceById(String deviceId);

    /** 查询所有设备 */
    List<Device> getAllDevices();

    /** 根据产品ID查询设备列表 */
    List<Device> getDevicesByProductId(String productId);

    /** 更新设备在线状态 */
    void updateDeviceStatus(String deviceId, String status);

    /** 记录设备上下线 */
    void recordConnectEvent(String deviceId, String eventType);

    /** 查询设备上下线记录 */
    List<DeviceConnectLog> getConnectLogs(String deviceId);

    /** 查询最近的上下线记录 */
    List<DeviceConnectLog> getRecentConnectLogs();

    /** 下发命令 */
    CommandLog sendCommand(String deviceId, String commandName, String commandParams);

    /** 更新命令执行结果 */
    void updateCommandResult(String commandId, String status, String result);

    /** 查询设备命令记录 */
    List<CommandLog> getCommandLogs(String deviceId);

    /** 统计在线设备数量 */
    long countOnlineDevices();

    /** 统计总设备数量 */
    long countTotalDevices();
}
