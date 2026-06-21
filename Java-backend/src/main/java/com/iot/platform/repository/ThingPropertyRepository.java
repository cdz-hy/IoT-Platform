package com.iot.platform.repository;

import com.iot.platform.entity.ThingProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物模型属性数据访问层
 */
@Repository
public interface ThingPropertyRepository extends JpaRepository<ThingProperty, String> {

    /** 根据产品ID查询所有属性 */
    List<ThingProperty> findByProductId(String productId);

    /** 根据产品ID和属性名查询 */
    ThingProperty findByProductIdAndPropertyName(String productId, String propertyName);
}
