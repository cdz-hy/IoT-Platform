package com.iot.platform.repository;

import com.iot.platform.entity.DeviceShadow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 设备影子数据访问层
 */
@Repository
public interface DeviceShadowRepository extends JpaRepository<DeviceShadow, String> {
}
