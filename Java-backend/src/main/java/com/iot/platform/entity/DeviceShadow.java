package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 设备影子 - 存储设备当前状态信息的JSON文档
 * 包含reported（设备上报）、desired（平台期望）、delta（差异）三个区域
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_shadow")
public class DeviceShadow {

    /** 设备ID */
    @Id
    @Column(name = "device_id", length = 64)
    private String deviceId;

    /** 影子数据JSON（包含reported/desired/delta/version） */
    @Column(name = "shadow_data", columnDefinition = "JSON")
    private String shadowData;

    /** 版本号（用于并发控制） */
    @Column(name = "version")
    private Long version = 1L;

    /** 更新时间 */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /** 关联设备 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    @JsonIgnore
    private Device device;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}
