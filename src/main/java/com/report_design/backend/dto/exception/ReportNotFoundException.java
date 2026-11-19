package com.report_design.backend.dto.exception;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String id) {
        super("Report template not found: " + id);
    }
}
