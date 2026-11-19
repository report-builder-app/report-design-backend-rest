package com.report_design.backend.controller;

import com.report_design.backend.dto.ReportListItemDto;
import com.report_design.backend.dto.SaveReportRequest;
import com.report_design.backend.dto.SavedReportDto;
import com.report_design.backend.dto.exception.ReportNotFoundException;
import com.report_design.backend.repository.JpaReportRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportTemplateController {

    private final JpaReportRepository repository;

    @GetMapping
    public List<ReportListItemDto> list() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<SavedReportDto> create(@Valid @RequestBody SaveReportRequest request) {
        SavedReportDto saved = repository.save(request.definition(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedReportDto> get(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ReportNotFoundException(id));
    }

    @PutMapping("/{id}")
    public SavedReportDto update(@PathVariable String id,
                                 @Valid @RequestBody SaveReportRequest request) {
        return repository.findById(id)
                .map(existing -> repository.save(request.definition(), id))
                .orElseThrow(() -> new ReportNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        if (!repository.delete(id)) {
            throw new ReportNotFoundException(id);
        }
    }
}
