package com.report_design.backend.dto;

public record DataSourceTestResult(
        boolean success,
        String message,
        Long elapsedMs,
        Integer sampleRows
) {}
