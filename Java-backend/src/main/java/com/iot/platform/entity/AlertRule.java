package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

/**
 * 告警规则 - 为设备属性值配置告警条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alert_rule")
public class AlertRule {

    /** 规则ID */
    @Id
    @Column(name = "id", length = 64)
    private String id;

    /** 产品ID */
    @Column(name = "product_id", length = 64)
    private String productId;

    /** 属性名称 */
    @Column(name = "property_name", length = 50)
    private String propertyName;

    /** 条件表达式（如 "> 35", "< 10"） */
    @Column(name = "condition_expr", length = 255)
    private String conditionExpr;

    /** 告警内容 */
    @Column(name = "alert_content", columnDefinition = "TEXT")
    private String alertContent;

    /** 是否启用 */
    @Column(name = "enabled")
    private Boolean enabled = true;
}
