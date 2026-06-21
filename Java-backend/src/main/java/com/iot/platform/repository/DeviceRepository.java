package com.iot.platform.repository;

import com.iot.platform.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 设备数据访问层
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {

    /** 根据产品ID查询设备列表 */
    List<Device> findByProductId(String productId);

    /** 根据设备ID查询（关联加载产品信息） */
    @Query("SELECT d FROM Device d JOIN FETCH d.product WHERE d.deviceId = :deviceId")
    Optional<Device> findByIdWithProduct(@Param("deviceId") String deviceId);

    /** 根据设备标识码查询 */
    Device findByNodeId(String nodeId);

    /** 统计产品下的设备数量 */
    long countByProductId(String productId);

    /** 统计在线设备数量 */
    long countByStatus(String status);

    /** 根据状态查询设备 */
    List<Device> findByStatus(String status);

    /** 按产品统计设备数量 */
    @Query("SELECT d.product.id, COUNT(d) FROM Device d GROUP BY d.product.id")
    List<Object[]> countByProductIdGroupBy();
}
