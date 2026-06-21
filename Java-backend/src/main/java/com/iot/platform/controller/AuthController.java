package com.iot.platform.controller;

import com.iot.platform.annotation.OperationLog;
import com.iot.platform.dto.ApiResponse;
import com.iot.platform.dto.LoginRequest;
import com.iot.platform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录认证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 管理员登录
     */
    @Operation(summary = "管理员登录")
    @OperationLog(module = "认证管理", type = "LOGIN", description = "管理员登录")
    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, String> result = authService.login(request.getUsername(), request.getPassword());
            return ApiResponse.success("登录成功", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }

    /**
     * 验证token
     */
    @Operation(summary = "验证token")
    @GetMapping("/validate")
    public ApiResponse<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean valid = authService.validateToken(token);
            return ApiResponse.success(valid);
        }
        return ApiResponse.error(401, "无效的token");
    }
}
