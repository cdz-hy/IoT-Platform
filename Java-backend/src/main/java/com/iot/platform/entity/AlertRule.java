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
@Table(name = "alert_rule")
public class AlertRule {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "product_id", length = 64, nullable = false)
    private String productId;

    @Column(name = "property_identifier", length = 50, nullable = false)
    private String propertyIdentifier;

    @Column(name = "operator", length = 10, nullable = false)
    private String operator;

    @Column(name = "threshold", length = 50, nullable = false)
    private String threshold;

    @Column(name = "alert_content", length = 255, nullable = false)
    private String alertContent;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}
