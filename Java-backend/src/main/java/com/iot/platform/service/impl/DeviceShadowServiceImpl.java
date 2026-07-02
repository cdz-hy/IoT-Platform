package com.iot.platform.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.entity.DeviceShadow;
import com.iot.platform.entity.DeviceShadowLog;
import com.iot.platform.repository.DeviceShadowLogRepository;
import com.iot.platform.repository.DeviceShadowRepository;
import com.iot.platform.service.DeviceShadowService;
import com.iot.platform.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceShadowServiceImpl implements DeviceShadowService {

    private final DeviceShadowRepository shadowRepository;
    private final DeviceShadowLogRepository shadowLogRepository;
    private final ObjectMapper objectMapper;
    private final WebSocketService webSocketService;

    @Override
    @Transactional
    public DeviceShadow getShadow(String deviceId) {
        return shadowRepository.findById(deviceId).orElseGet(() -> {
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
        String beforeState = shadow.getShadowData();
        Map<String, Object> shadowData = parseShadowData(beforeState);

        Map<String, Object> state = getOrCreateMap(shadowData, "state");
        Map<String, Object> reportedState = getOrCreateMap(state, "reported");
        reportedState.putAll(reported);
        state.put("reported", reportedState);
        shadowData.put("state", state);

        Map<String, Object> desired = getOrCreateMap(state, "desired");
        shadowData.put("delta", calculateDeltaInternal(desired, reportedState));

        shadow.setVersion(shadow.getVersion() + 1);
        shadowData.put("version", shadow.getVersion());
        String afterState = writeShadowData(shadowData);

        shadow.setShadowData(afterState);
        shadow.setUpdatedTime(LocalDateTime.now());
        shadowRepository.save(shadow);

        // 记录影子变更日志
        saveShadowLog(deviceId, shadow.getVersion(), "reported", beforeState, afterState);

        // WebSocket推送
        try {
            webSocketService.sendDeviceTelemetry(deviceId, Map.of("shadow", shadowData));
        } catch (Exception e) {
            log.warn("WebSocket推送影子变更失败: {}", e.getMessage());
        }

        log.info("设备影子reported更新: deviceId={}", deviceId);
    }

    @Override
    @Transactional
    public void updateDesired(String deviceId, Map<String, Object> desired) {
        DeviceShadow shadow = getShadow(deviceId);
        String beforeState = shadow.getShadowData();
        Map<String, Object> shadowData = parseShadowData(beforeState);

        Map<String, Object> state = getOrCreateMap(shadowData, "state");
        state.put("desired", desired);
        shadowData.put("state", state);

        Map<String, Object> reported = getOrCreateMap(state, "reported");
        shadowData.put("delta", calculateDeltaInternal(desired, reported));

        shadow.setVersion(shadow.getVersion() + 1);
        shadowData.put("version", shadow.getVersion());
        String afterState = writeShadowData(shadowData);

        shadow.setShadowData(afterState);
        shadow.setUpdatedTime(LocalDateTime.now());
        shadowRepository.save(shadow);

        saveShadowLog(deviceId, shadow.getVersion(), "desired", beforeState, afterState);

        log.info("设备影子desired更新: deviceId={}", deviceId);
    }

    @Override
    public Map<String, Object> calculateDelta(String deviceId) {
        DeviceShadow shadow = getShadow(deviceId);
        Map<String, Object> shadowData = parseShadowData(shadow.getShadowData());
        Map<String, Object> state = getOrCreateMap(shadowData, "state");
        Map<String, Object> desired = getOrCreateMap(state, "desired");
        Map<String, Object> reported = getOrCreateMap(state, "reported");
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

    private void saveShadowLog(String deviceId, Long version, String changeType, String before, String after) {
        try {
            DeviceShadowLog logEntry = new DeviceShadowLog();
            logEntry.setDeviceId(deviceId);
            logEntry.setVersion(version);
            logEntry.setChangeType(changeType);
            logEntry.setBeforeState(before);
            logEntry.setAfterState(after);
            logEntry.setCreatedTime(LocalDateTime.now());
            shadowLogRepository.save(logEntry);
        } catch (Exception e) {
            log.warn("保存影子变更日志失败: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getOrCreateMap(Map<String, Object> parent, String key) {
        Object val = parent.get(key);
        if (val instanceof Map) return (Map<String, Object>) val;
        Map<String, Object> newMap = new HashMap<>();
        parent.put(key, newMap);
        return newMap;
    }

    private Map<String, Object> calculateDeltaInternal(Map<String, Object> desired, Map<String, Object> reported) {
        Map<String, Object> delta = new HashMap<>();
        for (Map.Entry<String, Object> entry : desired.entrySet()) {
            String key = entry.getKey();
            Object desiredValue = entry.getValue();
            Object reportedValue = reported.get(key);
            if (reportedValue == null || !desiredValue.equals(reportedValue)) {
                delta.put(key, desiredValue);
            }
        }
        return delta;
    }

    private String createDefaultShadowData() {
        Map<String, Object> data = new HashMap<>();
        data.put("version", 1);
        Map<String, Object> state = new HashMap<>();
        state.put("reported", new HashMap<>());
        state.put("desired", new HashMap<>());
        data.put("state", state);
        data.put("delta", new HashMap<>());
        return writeShadowData(data);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseShadowData(String json) {
        try {
            if (json == null || json.isEmpty()) return new HashMap<>();
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }

    private String writeShadowData(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
