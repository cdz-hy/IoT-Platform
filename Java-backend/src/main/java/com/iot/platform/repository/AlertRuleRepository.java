package com.iot.platform.repository;

import com.iot.platform.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 告警规则数据访问层
 */
@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, String> {

    /** 根据产品ID查询告警规则 */
    List<AlertRule> findByProductId(String productId);

    /** 根据产品ID和属性名查询启用的规则 */
    List<AlertRule> findByProductIdAndPropertyNameAndEnabled(String productId, String propertyName, Boolean enabled);
}
