package com.iot.platform.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库初始化器 - 启动时自动检查并创建数据库和表
 */
@Slf4j
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        // 检查是否使用MySQL
        if (!datasourceUrl.contains("mysql")) {
            log.info("使用H2内存数据库，跳过MySQL初始化检查");
            return;
        }

        try {
            // 1. 检查并创建数据库
            createDatabaseIfNotExists();

            // 2. 检查表是否存在，不存在则执行schema.sql
            if (!tablesExist()) {
                log.info("表不存在，开始执行schema.sql创建表结构...");
                executeSchemaScript();
            } else {
                log.info("数据库表已存在");
            }

            // 3. 检查初始数据是否存在
            if (isDataEmpty()) {
                log.info("数据为空，开始执行data.sql插入初始数据...");
                executeDataScript();
            } else {
                log.info("初始数据已存在");
            }

        } catch (Exception e) {
            log.error("数据库初始化检查失败", e);
        }
    }

    /**
     * 检查并创建数据库
     */
    private void createDatabaseIfNotExists() {
        try {
            // 从URL中提取数据库名
            String dbName = extractDatabaseName(datasourceUrl);
            // 构建不带数据库名的URL
            String baseUrl = datasourceUrl.substring(0, datasourceUrl.lastIndexOf("/"));

            try (Connection conn = DriverManager.getConnection(baseUrl, username, password);
                 Statement stmt = conn.createStatement()) {

                String sql = String.format(
                        "CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
                        dbName
                );
                stmt.execute(sql);
                log.info("数据库 '{}' 创建检查完成", dbName);
            }
        } catch (Exception e) {
            log.warn("数据库创建检查失败（可能已存在）: {}", e.getMessage());
        }
    }

    /**
     * 检查表是否存在
     */
    private boolean tablesExist() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'product'");
            return rs.next();
        } catch (Exception e) {
            log.warn("检查表是否存在失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查数据是否为空
     */
    private boolean isDataEmpty() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM product");
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        } catch (Exception e) {
            log.warn("检查数据是否为空失败: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 执行schema.sql脚本
     */
    private void executeSchemaScript() {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("schema.sql"));
            populator.setContinueOnError(true);
            populator.execute(dataSource);
            log.info("schema.sql执行完成");
        } catch (Exception e) {
            log.warn("schema.sql执行失败（可能表已存在）: {}", e.getMessage());
        }
    }

    /**
     * 执行data.sql脚本
     */
    private void executeDataScript() {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("data.sql"));
            populator.setContinueOnError(true);
            populator.execute(dataSource);
            log.info("data.sql执行完成");
        } catch (Exception e) {
            log.warn("data.sql执行失败（可能数据已存在）: {}", e.getMessage());
        }
    }

    /**
     * 从URL中提取数据库名
     */
    private String extractDatabaseName(String url) {
        // jdbc:mysql://localhost:3306/iot_platform?params...
        String path = url.substring(url.lastIndexOf("/") + 1);
        int queryIndex = path.indexOf("?");
        if (queryIndex > 0) {
            path = path.substring(0, queryIndex);
        }
        return path;
    }
}
