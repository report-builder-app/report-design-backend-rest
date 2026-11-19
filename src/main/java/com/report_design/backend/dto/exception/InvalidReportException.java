package com.report_design.backend.dto.exception;

import java.util.List;

public class InvalidReportException extends RuntimeException {
    private final List<String> errors;

    public InvalidReportException(List<String> errors) {
        super("Invalid report definition: " + String.join("; ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
