package com.bloodconnect.controller;

import com.bloodconnect.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;

    @Value("${DB_HOST:mysql-29ab75ac-babishbabish1012-278a.j.aivencloud.com}")
    private String dbHost;

    @Value("${DB_PORT:13080}")
    private String dbPort;

    @Value("${DB_NAME:defaultdb}")
    private String dbName;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth() {
        Map<String, Object> details = new HashMap<>();
        details.put("status", "UP");
        details.put("application", "BloodConnect");
        details.put("timestamp", LocalDateTime.now());
        details.put("targetDatabase", dbHost + ":" + dbPort + "/" + dbName);

        boolean dbConnected = false;
        try (Connection conn = dataSource.getConnection()) {
            dbConnected = conn.isValid(2);
            details.put("databaseConnected", dbConnected);
            details.put("databaseProduct", conn.getMetaData().getDatabaseProductName());
        } catch (Exception ex) {
            details.put("databaseConnected", false);
            details.put("databaseError", ex.getMessage());
        }

        if (dbConnected) {
            return ResponseEntity.ok(ApiResponse.success("System and Cloud Database Healthy", details));
        } else {
            return ResponseEntity.status(200).body(ApiResponse.error("System running, database connection pending", details));
        }
    }
}
