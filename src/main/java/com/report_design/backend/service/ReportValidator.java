package com.report_design.backend.service;

import com.report_design.backend.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportValidator {
    public ValidationResult validate(ReportDefinitionDto dto) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (dto.name() == null || dto.name().isBlank()) {
            errors.add("Report name must not be blank");
        }

        if (dto.bands() == null || dto.bands().isEmpty()) {
            errors.add("At least one band must be defined");
        } else {
            boolean hasDetail = dto.bands().stream()
                    .anyMatch(b -> "DETAIL".equals(b.type()));
            if (!hasDetail) {
                warnings.add("No DETAIL band: report will render static content only");
            }
            dto.bands().forEach(b -> b.elements().forEach(e -> {
                if ("textField".equals(e.kind()) && e.expression() == null) {
                    errors.add("textField element missing 'expression' in band " + b.type());
                }
                if ("label".equals(e.kind()) && e.text() == null) {
                    errors.add("label element missing 'text' in band " + b.type());
                }
            }));
        }

        if (dto.dataSource() == null) {
            warnings.add("No dataSource: report will render without data");
        } else {
            try {
                validateDataSource(dto.dataSource());
            } catch (IllegalArgumentException e) {
                errors.add("dataSource: " + e.getMessage());
            }
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    private void validateDataSource(DataSourceDto ds) {
        switch (ds.provider()) {
            case "sql" -> {
                if (ds.jdbcUrl() == null || ds.jdbcUrl().isBlank())
                    throw new IllegalArgumentException("jdbcUrl is required for sql provider");
            }
            case "web" -> {
                if (ds.url() == null || ds.url().isBlank())
                    throw new IllegalArgumentException("url is required for web provider");
            }
            case "custom" -> {
                if (ds.options() == null || !ds.options().containsKey("providerName"))
                    throw new IllegalArgumentException("options.providerName is required for custom provider");
            }
            default -> throw new IllegalArgumentException("Unknown provider: " + ds.provider());
        }
    }
}
