package com.report_design.backend.dto;

import java.time.Instant;
import java.util.Map;

public record DataSourceResponse(
        String id,
        String name,
        String provider,
        String jdbcUrl,
        String user,
        String url,
        String jsonPath,
        Map<String, Object> options,
        Instant createdAt,
        Instant updatedAt
) {}
