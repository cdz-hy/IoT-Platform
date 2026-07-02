package com.iot.platform.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alert_record")
public class AlertRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", length = 64, nullable = false)
    private String deviceId;

    @Column(name = "product_id", length = 64, nullable = false)
    private String productId;

    @Column(name = "rule_id", length = 64)
    private String ruleId;

    @Column(name = "property_identifier", length = 50, nullable = false)
    private String propertyIdentifier;

    @Column(name = "actual_value", length = 50, nullable = false)
    private String actualValue;

    @Column(name = "alert_content", length = 255, nullable = false)
    private String alertContent;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "pending";

    @Column(name = "alert_time", nullable = false)
    private LocalDateTime alertTime;

    @Column(name = "confirmed_time")
    private LocalDateTime confirmedTime;

    @Column(name = "remark", length = 255)
    private String remark;

    @Transient
    private String deviceName;

    @PrePersist
    protected void onCreate() {
        alertTime = LocalDateTime.now();
    }
}
