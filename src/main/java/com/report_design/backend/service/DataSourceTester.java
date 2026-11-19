package com.report_design.backend.service;

import com.report_design.backend.dto.DataSourceDto;
import com.report_design.backend.dto.DataSourceTestResult;
import com.report_design_lib.core.data_source.*;
import com.report_design_lib.interfaces.DataSourceProvider;
import com.report_design_lib.interfaces.ReportDataSource;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

@Service
public class DataSourceTester {

    public DataSourceTestResult test(DataSourceDto dto) {
        long start = System.currentTimeMillis();
        try {
            return switch (dto.provider()) {
                case "sql" -> testSql(dto, start);
                case "web" -> testWeb(dto, start);
                case "custom" -> testCustom(dto, start);
                default -> new DataSourceTestResult(
                        false,
                        "Unknown provider: " + dto.provider(),
                        System.currentTimeMillis() - start,
                        null
                );
            };
        } catch (Exception e) {
            return new DataSourceTestResult(
                    false,
                    e.getMessage(),
                    System.currentTimeMillis() - start,
                    null
            );
        }
    }

    // ---------- SQL ----------
    private DataSourceTestResult testSql(DataSourceDto dto, long start) {
        try (Connection c = DriverManager.getConnection(
                dto.jdbcUrl(), dto.user(), dto.password())) {
            boolean valid = c.isValid(3); // 3 секунды таймаут
            return new DataSourceTestResult(
                    valid,
                    valid ? "Connected" : "Connection is not valid",
                    System.currentTimeMillis() - start,
                    null
            );
        } catch (Exception e) {
            return new DataSourceTestResult(
                    false,
                    "SQL error: " + e.getMessage(),
                    System.currentTimeMillis() - start,
                    null
            );
        }
    }

    // ---------- Web ----------
    private DataSourceTestResult testWeb(DataSourceDto dto, long start) {
        try {
            var conn = (java.net.HttpURLConnection) new java.net.URL(dto.url()).openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            return new DataSourceTestResult(
                    code < 400,
                    "HTTP " + code,
                    System.currentTimeMillis() - start,
                    null
            );
        } catch (Exception e) {
            return new DataSourceTestResult(
                    false,
                    "HTTP error: " + e.getMessage(),
                    System.currentTimeMillis() - start,
                    null
            );
        }
    }

    // ---------- Custom ----------
    private DataSourceTestResult testCustom(DataSourceDto dto, long start) {
        if (dto.options() == null || !dto.options().containsKey("providerName")) {
            return new DataSourceTestResult(
                    false,
                    "options.providerName is required for custom provider",
                    System.currentTimeMillis() - start,
                    null
            );
        }
        try {
            String providerName = dto.options().get("providerName").toString();
            CustomConfig cfg = new CustomConfig(providerName, dto.options());
            DataSourceProvider<CustomConfig> provider = DataSourceRegistry.getTyped(providerName, CustomConfig.class);
            ReportDataSource ds = provider.create(cfg, Map.of());

            int count = 0;
            while (ds.next() && count < 5) {
                count++;
            }
            return new DataSourceTestResult(
                    true,
                    "OK",
                    System.currentTimeMillis() - start,
                    count
            );
        } catch (Exception e) {
            return new DataSourceTestResult(
                    false,
                    "Custom provider error: " + e.getMessage(),
                    System.currentTimeMillis() - start,
                    null
            );
        }
    }
}
