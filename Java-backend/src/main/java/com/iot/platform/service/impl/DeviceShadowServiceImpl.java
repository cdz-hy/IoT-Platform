package com.iot.platform.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.entity.DeviceShadow;
import com.iot.platform.repository.DeviceShadowRepository;
import com.iot.platform.service.DeviceShadowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 设备影子服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceShadowServiceImpl implements DeviceShadowService {

    private final DeviceShadowRepository shadowRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public DeviceShadow getShadow(String deviceId) {
        return shadowRepository.findById(deviceId).orElseGet(() -> {
            // 不存在则创建新的影子文档
            DeviceShadow shadow = new DeviceShadow();
            shadow.setDeviceId(deviceId);
            shadow.setVersion(1L);
            shadow.setShadowData(createDefaultShadowData());
            shadow.setUpdatedTime(LocalDateTime.now());
            return shadowRepository.save(shadow);
        });
    }

    @Override
    @Transactional
    public void updateReported(String deviceId, Map<String, Object> reported) {
        DeviceShadow shadow = getShadow(deviceId);
        Map<String, Object> shadowData = parseShadowData(shadow.getShadowData());

        // 更新reported区
        Map<String, Object> state = (Map<String, Object>) shadowData.getOrDefault("state", new HashMap<>());
        Map<String, Object> reportedState = (Map<String, Object>) state.getOrDefault("reported", new HashMap<>());
        reportedState.putAll(reported);
        state.put("reported", reportedState);
        shadowData.put("state", state);

        // 重新计算delta
        Map<String, Object> desired = (Map<String, Object>) state.getOrDefault("desired", new HashMap<>());
        shadowData.put("delta", calculateDeltaInternal(desired, reportedState));

        // 更新版本号
        shadow.setVersion(shadow.getVersion() + 1);
        shadowData.put("version", shadow.getVersion());

        shadow.setShadowData(writeShadowData(shadowData));
        shadow.setUpdatedTime(LocalDateTime.now());
        shadowRepository.save(shadow);

        log.info("设备影子reported更新: deviceId={}", deviceId);
    }

    @Override
    @Transactional
    public void updateDesired(String deviceId, Map<String, Object> desired) {
        DeviceShadow shadow = getShadow(deviceId);
        Map<String, Object> shadowData = parseShadowData(shadow.getShadowData());

        // 更新desired区
        Map<String, Object> state = (Map<String, Object>) shadowData.getOrDefault("state", new HashMap<>());
        state.put("desired", desired);
        shadowData.put("state", state);

        // 重新计算delta
        Map<String, Object> reported = (Map<String, Object>) state.getOrDefault("reported", new HashMap<>());
        shadowData.put("delta", calculateDeltaInternal(desired, reported));

        // 更新版本号
        shadow.setVersion(shadow.getVersion() + 1);
        shadowData.put("version", shadow.getVersion());

        shadow.setShadowData(writeShadowData(shadowData));
        shadow.setUpdatedTime(LocalDateTime.now());
        shadowRepository.save(shadow);

        log.info("设备影子desired更新: deviceId={}", deviceId);
    }

    @Override
    public Map<String, Object> calculateDelta(String deviceId) {
        DeviceShadow shadow = getShadow(deviceId);
        Map<String, Object> shadowData = parseShadowData(shadow.getShadowData());

        Map<String, Object> state = (Map<String, Object>) shadowData.getOrDefault("state", new HashMap<>());
        Map<String, Object> desired = (Map<String, Object>) state.getOrDefault("desired", new HashMap<>());
        Map<String, Object> reported = (Map<String, Object>) state.getOrDefault("reported", new HashMap<>());

        return calculateDeltaInternal(desired, reported);
    }

    @Override
    @Transactional
    public Map<String, Object> syncDeltaOnConnect(String deviceId) {
        Map<String, Object> delta = calculateDelta(deviceId);
        if (!delta.isEmpty()) {
            log.info("设备上线同步delta: deviceId={}, delta={}", deviceId, delta);
        }
        return delta;
    }

    /**
     * 计算desired与reported的差异
     */
    private Map<String, Object> calculateDeltaInternal(Map<String, Object> desired, Map<String, Object> reported) {
        Map<String, Object> delta = new HashMap<>();
        for (Map.Entry<String, Object> entry : desired.entrySet()) {
            String key = entry.getKey();
            Object desiredValue = entry.getValue();
            Object reportedValue = reported.get(key);

            // 如果reported中没有该属性，或者值不同，则加入delta
            if (reportedValue == null || !desiredValue.equals(reportedValue)) {
                delta.put(key, desiredValue);
            }
        }
        return delta;
    }

    /**
     * 创建默认的影子数据
     */
    private String createDefaultShadowData() {
        Map<String, Object> data = new HashMap<>();
        data.put("version", 1L);

        Map<String, Object> state = new HashMap<>();
        state.put("reported", new HashMap<>());
        state.put("desired", new HashMap<>());
        data.put("state", state);
        data.put("delta", new HashMap<>());

        return writeShadowData(data);
    }

    /**
     * 解析影子JSON数据
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseShadowData(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.error("解析影子数据失败", e);
            return new HashMap<>();
        }
    }

    /**
     * 序列化影子数据为JSON
     */
    private String writeShadowData(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("序列化影子数据失败", e);
            return "{}";
        }
    }
}
