package com.report_design.backend.dto;

import java.time.Instant;

public record ReportListItemDto(
        String id,
        String name,
        String title,
        Instant updatedAt
) {}
