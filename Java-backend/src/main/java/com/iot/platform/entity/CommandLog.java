package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 命令记录 - 记录平台向设备下发的历史命令及执行结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "command_log")
public class CommandLog {

    /** 记录ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 设备ID */
    @Column(name = "device_id", length = 64)
    private String deviceId;

    /** 命令名称 */
    @Column(name = "command_name", length = 50)
    private String commandName;

    /** 命令参数（JSON格式） */
    @Column(name = "command_params", columnDefinition = "TEXT")
    private String commandParams;

    /** 命令状态：pending, success, failed */
    @Column(name = "status", length = 20)
    private String status = "pending";

    /** 执行结果 */
    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    /** 请求时间 */
    @Column(name = "request_time")
    private LocalDateTime requestTime;

    /** 响应时间 */
    @Column(name = "response_time")
    private LocalDateTime responseTime;

    /** 命令ID（用于MQTT主题） */
    @Column(name = "command_id", length = 64)
    private String commandId;

    @PrePersist
    protected void onCreate() {
        requestTime = LocalDateTime.now();
    }
}
