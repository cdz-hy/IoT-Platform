package com.iot.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 设备影子更新请求DTO
 */
@Data
public class ShadowUpdateRequest {

    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /** desired属性值 */
    private Map<String, Object> desired;
}
