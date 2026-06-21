package com.iot.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 命令下发请求DTO
 */
@Data
public class CommandRequest {

    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @NotBlank(message = "命令名称不能为空")
    private String commandName;

    /** 命令参数（JSON格式） */
    private String commandParams;
}
