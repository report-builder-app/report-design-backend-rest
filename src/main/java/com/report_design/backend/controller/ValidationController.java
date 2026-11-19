package com.report_design.backend.controller;

import com.report_design.backend.dto.RenderRequest;
import com.report_design.backend.dto.ValidationResult;
import com.report_design.backend.service.ReportValidator;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ValidationController {

    private final ReportValidator validator;

    public ValidationController(ReportValidator validator) {
        this.validator = validator;
    }

    @PostMapping("/validate")
    public ValidationResult validate(@Valid @RequestBody RenderRequest request) {
        return validator.validate(request.report());
    }
}
