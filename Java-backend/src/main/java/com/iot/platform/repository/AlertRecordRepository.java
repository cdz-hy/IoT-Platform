package com.iot.platform.repository;

import com.iot.platform.entity.AlertRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警记录数据访问层
 */
@Repository
public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {

    /** 根据设备ID查询告警记录 */
    List<AlertRecord> findByDeviceIdOrderByAlertTimeDesc(String deviceId);

    /** 查询最近的告警记录 */
    List<AlertRecord> findTop20ByOrderByAlertTimeDesc();

    /** 根据状态查询 */
    List<AlertRecord> findByStatus(String status);

    /** 统计今日告警数量 */
    @Query("SELECT COUNT(a) FROM AlertRecord a WHERE a.alertTime >= :start")
    long countTodayAlerts(LocalDateTime start);
}
