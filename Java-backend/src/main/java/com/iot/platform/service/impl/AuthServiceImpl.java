package com.iot.platform.service.impl;

import com.iot.platform.entity.SysUser;
import com.iot.platform.repository.SysUserRepository;
import com.iot.platform.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey signingKey;

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(SysUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void register(String username, String password, String nickname) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setRole("admin");
        user.setEnabled(true);
        userRepository.save(user);
        log.info("用户注册成功: {}", username);
    }

    @Override
    public Map<String, String> login(String username, String password) {
        SysUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!user.getEnabled()) {
            throw new RuntimeException("账号已被禁用");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 更新最后登录时间
        try {
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);
        } catch (Exception e) {
            log.warn("更新最后登录时间失败: {}", e.getMessage());
        }

        String token = Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey)
                .compact();

        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("role", user.getRole());
        return result;
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT token验证失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
