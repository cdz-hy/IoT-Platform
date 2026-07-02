package com.iot.platform.repository;

import com.iot.platform.entity.AlertRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {
    List<AlertRecord> findByDeviceIdOrderByAlertTimeDesc(String deviceId);
    List<AlertRecord> findByStatus(String status);

    @Query("SELECT a FROM AlertRecord a ORDER BY a.alertTime DESC")
    List<AlertRecord> findRecentAlerts(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(a) FROM AlertRecord a WHERE a.alertTime >= :start")
    long countTodayAlerts(@Param("start") LocalDateTime start);
}
