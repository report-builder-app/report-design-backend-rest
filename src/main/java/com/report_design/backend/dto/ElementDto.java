package com.report_design.backend.dto;

public record ElementDto(
        String kind,              // label, textField, line
        String text,
        String expression,
        String style,
        Float width,
        Float height,
        Float y
) {}
