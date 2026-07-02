package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_connect_log")
public class DeviceConnectLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", length = 64, nullable = false)
    private String deviceId;

    @Column(name = "event_type", length = 20, nullable = false)
    private String eventType;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "client_id", length = 128)
    private String clientId;

    @Column(name = "reason", length = 255)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    @JsonIgnore
    private Device device;

    @PrePersist
    protected void onCreate() {
        eventTime = LocalDateTime.now();
    }
}
