package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

/**
 * 物模型属性 - 定义设备的可读/写数据项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "thing_property")
public class ThingProperty {

    /** 属性ID */
    @Id
    @Column(name = "id", length = 64)
    private String id;

    /** 属性名称 */
    @Column(name = "property_name", length = 50)
    private String propertyName;

    /** 数据类型：int, double, boolean, string */
    @Column(name = "data_type", length = 20)
    private String dataType;

    /** 最小值 */
    @Column(name = "min_value", length = 50)
    private String minValue;

    /** 最大值 */
    @Column(name = "max_value", length = 50)
    private String maxValue;

    /** 单位 */
    @Column(name = "unit", length = 20)
    private String unit;

    /** 访问权限：read, write, read_write */
    @Column(name = "access_mode", length = 10)
    private String accessMode;

    /** 所属产品 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;
}
