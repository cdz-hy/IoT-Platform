package com.iot.platform.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品实体 - 同一类设备的抽象模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    /** 产品ID */
    @Id
    @Column(name = "id", length = 64)
    private String id;

    /** 产品名称 */
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    /** 产品类型（如温湿度传感器、智能灯等） */
    @Column(name = "type", length = 30)
    private String type;

    /** 创建时间 */
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /** 物模型属性列表 */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThingProperty> properties = new ArrayList<>();

    /** 物模型服务列表 */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThingService> services = new ArrayList<>();

    /** 物模型事件列表 */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThingEvent> events = new ArrayList<>();

    /** 设备列表 */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Device> devices = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }
}
