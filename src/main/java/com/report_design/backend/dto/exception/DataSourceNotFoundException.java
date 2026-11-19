package com.report_design.backend.dto.exception;

public class DataSourceNotFoundException extends RuntimeException {
    public DataSourceNotFoundException(String id) {
        super("Data source not found: " + id);
    }
}
