package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

/**
 * 物模型服务 - 定义设备可被远程调用的指令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "thing_service")
public class ThingService {

    /** 服务ID */
    @Id
    @Column(name = "id", length = 64)
    private String id;

    /** 服务名称 */
    @Column(name = "service_name", length = 50)
    private String serviceName;

    /** 输入参数（JSON格式） */
    @Column(name = "input_params", columnDefinition = "TEXT")
    private String inputParams;

    /** 输出参数（JSON格式） */
    @Column(name = "output_params", columnDefinition = "TEXT")
    private String outputParams;

    /** 所属产品 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;
}
