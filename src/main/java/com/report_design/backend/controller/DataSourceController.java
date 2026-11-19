package com.report_design.backend.controller;

import com.report_design.backend.dto.*;
import com.report_design.backend.dto.exception.DataSourceNotFoundException;
import com.report_design.backend.service.DataSourceService;
import com.report_design.backend.service.DataSourceTester;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/datasources")
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceTester tester;
    private final DataSourceService service;

    @GetMapping
    public List<DataSourceResponse> list() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<DataSourceResponse> create(@Valid @RequestBody DataSourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public DataSourceResponse get(@PathVariable String id) {
        return service.findById(id)
                .orElseThrow(() -> new DataSourceNotFoundException(id));
    }

    @PutMapping("/{id}")
    public DataSourceResponse update(@PathVariable String id,
                                     @Valid @RequestBody DataSourceRequest request) {
        return service.update(id, request)
                .orElseThrow(() -> new DataSourceNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        if (!service.delete(id)) {
            throw new DataSourceNotFoundException(id);
        }
    }

    @PostMapping("/{id}/test")
    public DataSourceTestResult test(@PathVariable String id) {
        return service.test(id);
    }
}
