package com.report_design.backend.dto;

import java.util.List;
import java.util.Map;

public record ReportDefinitionDto(
        String name,
        String title,
        String pageFormat,        // A4, A5, LETTER
        String orientation,       // PORTRAIT, LANDSCAPE
        List<ParameterDto> parameters,
        List<FieldDto> fields,
        String queryString,
        List<BandDto> bands,
        DataSourceDto dataSource
) {}
