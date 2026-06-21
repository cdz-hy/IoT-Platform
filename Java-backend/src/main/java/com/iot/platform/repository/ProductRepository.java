package com.iot.platform.repository;

import com.iot.platform.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 产品数据访问层
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /** 根据产品类型查询 */
    List<Product> findByType(String type);

    /** 统计各类型产品数量 */
    @Query("SELECT p.type, COUNT(p) FROM Product p GROUP BY p.type")
    List<Object[]> countByType();
}
