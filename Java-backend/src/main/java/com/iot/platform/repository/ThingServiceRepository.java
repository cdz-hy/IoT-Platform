package com.iot.platform.repository;

import com.iot.platform.entity.ThingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物模型服务数据访问层
 */
@Repository
public interface ThingServiceRepository extends JpaRepository<ThingService, String> {

    /** 根据产品ID查询所有服务 */
    List<ThingService> findByProductId(String productId);
}
