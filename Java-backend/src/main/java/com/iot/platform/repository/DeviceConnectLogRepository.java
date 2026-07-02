package com.iot.platform.repository;

import com.iot.platform.entity.DeviceConnectLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备上下线记录数据访问层
 */
@Repository
public interface DeviceConnectLogRepository extends JpaRepository<DeviceConnectLog, Long> {

    /** 根据设备ID查询上下线记录（按时间倒序） */
    List<DeviceConnectLog> findByDeviceIdOrderByEventTimeDesc(String deviceId);

    /** 查询最近的上下线记录 */
    List<DeviceConnectLog> findTop20ByOrderByEventTimeDesc();

    /** 统计指定时间范围内的上下线次数 */
    @Query("SELECT d.eventType, COUNT(d) FROM DeviceConnectLog d WHERE d.eventTime BETWEEN :start AND :end GROUP BY d.eventType")
    List<Object[]> countByEventTypeBetween(LocalDateTime start, LocalDateTime end);

    /** 按天统计上下线次数（近7天趋势图） */
    @Query(value = "SELECT DATE(event_time) as date, event_type, COUNT(*) as cnt " +
            "FROM device_connect_log " +
            "WHERE event_time >= :start " +
            "GROUP BY DATE(event_time), event_type " +
            "ORDER BY date", nativeQuery = true)
    List<Object[]> countDailyByEventType(@org.springframework.data.repository.query.Param("start") LocalDateTime start);
}
