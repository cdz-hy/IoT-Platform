package com.iot.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 设备注册请求DTO
 */
@Data
public class DeviceRegisterRequest {

    @NotBlank(message = "产品ID不能为空")
    private String productId;

    @NotBlank(message = "设备标识码不能为空")
    private String nodeId;

    private String deviceName;
}
