package com.iot.platform.service;

import com.iot.platform.entity.DeviceShadow;

import java.util.Map;

/**
 * 设备影子服务接口
 */
public interface DeviceShadowService {

    /** 获取设备影子 */
    DeviceShadow getShadow(String deviceId);

    /** 更新设备reported区（设备上报数据时调用） */
    void updateReported(String deviceId, Map<String, Object> reported);

    /** 更新设备desired区（管理员设置期望值） */
    void updateDesired(String deviceId, Map<String, Object> desired);

    /** 计算delta（desired与reported的差异） */
    Map<String, Object> calculateDelta(String deviceId);

    /** 设备上线时同步delta */
    Map<String, Object> syncDeltaOnConnect(String deviceId);
}
