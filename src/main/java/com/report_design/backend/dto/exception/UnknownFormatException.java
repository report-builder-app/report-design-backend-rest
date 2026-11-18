package com.report_design.backend.dto.exception;

public class UnknownFormatException extends RuntimeException {
    public UnknownFormatException(String format) {
        super("Unsupported format: " + format);
    }
}
