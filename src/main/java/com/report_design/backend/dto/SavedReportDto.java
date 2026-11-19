package com.report_design.backend.dto;

import java.time.Instant;

public record SavedReportDto(
        String id,
        ReportDefinitionDto definition,
        Instant createdAt,
        Instant updatedAt
) {}
