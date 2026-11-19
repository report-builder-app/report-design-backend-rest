package com.report_design.backend.dto;

public record StyleDto(
        String name,
        String fontName,
        Integer fontSize,
        Boolean bold,
        Boolean italic,
        String foreground,
        String background
) {}
