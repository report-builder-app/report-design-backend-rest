package com.report_design.backend.service;

import com.report_design.backend.dto.*;
import com.report_design_lib.core.data_source.*;
import com.report_design_lib.interfaces.DataSourceProvider;
import com.report_design_lib.interfaces.ReportDataSource;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;

import static tools.jackson.databind.type.LogicalType.Map;

@Service
public class DataSourceTester {

    public DataSourceTestResult test(DataSourceDto dto) {
        long start = System.currentTimeMillis();
        try {
            switch (dto.provider()) {
                case "sql" -> {
                    try (Connection c = DriverManager.getConnection(
                            dto.jdbcUrl(), dto.user(), dto.password())) {
                        return new DataSourceTestResult(true, "Connected",
                                System.currentTimeMillis() - start, null);
                    }
                }
                case "web" -> {
                    var conn = new java.net.URL(dto.url()).openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    int code = ((java.net.HttpURLConnection) conn).getResponseCode();
                    return new DataSourceTestResult(code < 400, "HTTP " + code,
                            System.currentTimeMillis() - start, null);
                }
                case "custom" -> {
                    CustomConfig cfg = toCustomConfig(dto);
                    DataSourceProvider<CustomConfig> provider =
                            DataSourceRegistry.getTyped(dto.options().get("providerName").toString(), CustomConfig.class);
                    ReportDataSource ds = provider.create(cfg, java.util.Map.of());
                    int count = 0;
                    while (ds.next() && count < 5) count++;
                    return new DataSourceTestResult(true, "OK",
                            System.currentTimeMillis() - start, count);
                }
                default -> {
                    return new DataSourceTestResult(false, "Unknown provider: " + dto.provider(),
                            System.currentTimeMillis() - start, null);
                }
            }
        } catch (Exception e) {
            return new DataSourceTestResult(false, e.getMessage(),
                    System.currentTimeMillis() - start, null);
        }
    }

    private CustomConfig toCustomConfig(DataSourceDto dto) {
        return new CustomConfig(
                dto.options().get("providerName").toString(),
                dto.options());
    }
}
