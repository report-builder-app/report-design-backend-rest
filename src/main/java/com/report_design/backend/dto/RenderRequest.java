package com.report_design.backend.dto;

import java.util.Map;

public record RenderRequest(
        ReportDefinitionDto report,
        Map<String, Object> parameters
) {}
