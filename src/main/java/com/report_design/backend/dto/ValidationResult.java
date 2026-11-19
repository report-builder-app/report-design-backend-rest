package com.report_design.backend.dto;

import java.util.List;

public record ValidationResult(
        boolean valid,
        List<String> errors,
        List<String> warnings
) {}
