package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

/**
 * 物模型事件 - 定义设备主动上报的异常或通知事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "thing_event")
public class ThingEvent {

    /** 事件ID */
    @Id
    @Column(name = "id", length = 64)
    private String id;

    /** 事件名称 */
    @Column(name = "event_name", length = 50)
    private String eventName;

    /** 事件类型 */
    @Column(name = "event_type", length = 30)
    private String eventType;

    /** 事件描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 所属产品 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;
}
