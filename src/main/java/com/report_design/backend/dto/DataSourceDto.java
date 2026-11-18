package com.report_design.backend.dto;

import java.util.Map;

public record DataSourceDto(
        String provider,          // sql, web, custom
        String jdbcUrl,
        String user,
        String password,
        String url,
        String jsonPath,
        Map<String, Object> options
) {}
