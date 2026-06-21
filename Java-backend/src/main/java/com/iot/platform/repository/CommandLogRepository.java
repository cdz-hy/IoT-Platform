package com.iot.platform.repository;

import com.iot.platform.entity.CommandLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 命令记录数据访问层
 */
@Repository
public interface CommandLogRepository extends JpaRepository<CommandLog, Long> {

    /** 根据设备ID查询命令记录（按时间倒序） */
    List<CommandLog> findByDeviceIdOrderByRequestTimeDesc(String deviceId);

    /** 根据命令ID查询 */
    CommandLog findByCommandId(String commandId);

    /** 根据状态查询 */
    List<CommandLog> findByStatus(String status);
}
