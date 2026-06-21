package com.iot.platform.repository;

import com.iot.platform.entity.ThingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物模型事件数据访问层
 */
@Repository
public interface ThingEventRepository extends JpaRepository<ThingEvent, String> {

    /** 根据产品ID查询所有事件 */
    List<ThingEvent> findByProductId(String productId);
}
