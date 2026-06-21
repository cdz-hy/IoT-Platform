package com.iot.platform.service;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {

    /** 管理员登录 */
    Map<String, String> login(String username, String password);

    /** 验证JWT token */
    boolean validateToken(String token);

    /** 从token中获取用户名 */
    String getUsernameFromToken(String token);
}
