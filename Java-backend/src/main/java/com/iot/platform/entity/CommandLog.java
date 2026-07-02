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
@Table(name = "command_log")
public class CommandLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "command_id", length = 64, unique = true, nullable = false)
    private String commandId;

    @Column(name = "device_id", length = 64, nullable = false)
    private String deviceId;

    @Column(name = "service_identifier", length = 50)
    private String serviceIdentifier;

    @Column(name = "command_name", length = 50, nullable = false)
    private String commandName;

    @Column(name = "command_params", columnDefinition = "TEXT")
    private String commandParams;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "pending";

    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "response_time")
    private LocalDateTime responseTime;

    @PrePersist
    protected void onCreate() {
        requestTime = LocalDateTime.now();
    }
}
