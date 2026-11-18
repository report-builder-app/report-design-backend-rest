package com.report_design.backend.dto;

import java.util.List;

public record BandDto(
        String type,              // TITLE, COLUMN_HEADER, DETAIL, SUMMARY
        List<ElementDto> elements
) {}
