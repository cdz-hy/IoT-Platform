package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 设备上下线记录 - 记录每次设备连接和断开的时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_connect_log")
public class DeviceConnectLog {

    /** 记录ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 设备ID */
    @Column(name = "device_id", length = 64)
    private String deviceId;

    /** 事件类型：online / offline */
    @Column(name = "event_type", length = 10)
    private String eventType;

    /** 事件时间 */
    @Column(name = "event_time")
    private LocalDateTime eventTime;

    /** 关联设备 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    @JsonIgnore
    private Device device;

    @PrePersist
    protected void onCreate() {
        eventTime = LocalDateTime.now();
    }
}
