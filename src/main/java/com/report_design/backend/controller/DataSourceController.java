package com.report_design.backend.controller;

import com.report_design.backend.dto.*;
import com.report_design.backend.service.DataSourceTester;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/datasources")
public class DataSourceController {

    private final DataSourceTester tester;

    public DataSourceController(DataSourceTester tester) {
        this.tester = tester;
    }

    @PostMapping("/test")
    public DataSourceTestResult test(@Valid @RequestBody DataSourceTestRequest request) {
        return tester.test(request.dataSource());
    }
}
