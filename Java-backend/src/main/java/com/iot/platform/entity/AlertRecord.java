package com.iot.platform.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 告警记录 - 记录触发的告警事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alert_record")
public class AlertRecord {

    /** 记录ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 设备ID */
    @Column(name = "device_id", length = 64)
    private String deviceId;

    /** 规则ID */
    @Column(name = "rule_id", length = 64)
    private String ruleId;

    /** 告警内容 */
    @Column(name = "alert_content", columnDefinition = "TEXT")
    private String alertContent;

    /** 告警时间 */
    @Column(name = "alert_time")
    private LocalDateTime alertTime;

    /** 状态：pending, confirmed */
    @Column(name = "status", length = 20)
    private String status = "pending";

    /** 确认时间 */
    @Column(name = "confirmed_time")
    private LocalDateTime confirmedTime;

    /** 备注 */
    @Column(name = "remark", length = 255)
    private String remark;

    /** 设备名称（冗余字段） */
    @Transient
    private String deviceName;

    @PrePersist
    protected void onCreate() {
        alertTime = LocalDateTime.now();
    }
}
