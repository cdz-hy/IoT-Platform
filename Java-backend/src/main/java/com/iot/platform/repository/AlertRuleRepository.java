package com.iot.platform.repository;

import com.iot.platform.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, String> {
    List<AlertRule> findByProductId(String productId);
    List<AlertRule> findByProductIdAndPropertyIdentifierAndEnabled(String productId, String propertyIdentifier, Boolean enabled);
    List<AlertRule> findByProductIdAndEnabled(String productId, Boolean enabled);
}
