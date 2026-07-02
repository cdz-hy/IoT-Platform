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
@Table(name = "device")
public class Device {

    @Id
    @Column(name = "device_id", length = 64)
    private String deviceId;

    @Column(name = "node_id", length = 64, unique = true, nullable = false)
    private String nodeId;

    @Column(name = "device_name", length = 50, nullable = false)
    private String deviceName;

    @Column(name = "location", length = 100)
    private String location;

    @JsonIgnore
    @Column(name = "secret", length = 128, nullable = false)
    private String secret;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "offline";

    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    @Column(name = "activated_time")
    private LocalDateTime activatedTime;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Transient
    private String productName;

    @Transient
    private String belongProductId;

    @PostLoad
    private void loadTransient() {
        if (this.product != null) {
            this.productName = this.product.getName();
            this.belongProductId = this.product.getId();
        }
    }

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }

    public String getProductName() {
        if (product != null) {
            return product.getName();
        }
        return productName;
    }
}
