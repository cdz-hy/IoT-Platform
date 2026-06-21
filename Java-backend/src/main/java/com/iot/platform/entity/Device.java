package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 设备实体 - 归属于某个产品，继承该产品的物模型定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device")
public class Device {

    /** 设备ID（全局唯一，平台分配） */
    @Id
    @Column(name = "device_id", length = 64)
    private String deviceId;

    /** 设备标识码（如IMEI/MAC/序列号，全局唯一） */
    @Column(name = "node_id", length = 64, unique = true, nullable = false)
    private String nodeId;

    /** 设备名称 */
    @Column(name = "device_name", length = 50)
    private String deviceName;

    /** 连接密钥 */
    @Column(name = "secret", length = 128)
    private String secret;

    /** 在线状态：online, offline */
    @Column(name = "status", length = 10)
    private String status = "offline";

    /** 最后上线时间 */
    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    /** 注册时间 */
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /** 所属产品 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    /** 产品名称（冗余字段，通过@PostLoad自动填充） */
    @Transient
    private String productName;

    /**
     * JPA加载后自动填充productName，避免懒加载序列化问题
     */
    @PostLoad
    private void loadProductName() {
        if (this.product != null) {
            this.productName = this.product.getName();
        }
    }

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }

    /**
     * 获取产品名称
     */
    public String getProductName() {
        if (product != null) {
            return product.getName();
        }
        return productName;
    }
}
