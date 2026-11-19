package com.report_design.backend.repository;

import com.report_design.backend.dto.ReportDefinitionDto;
import com.report_design.backend.dto.ReportListItemDto;
import com.report_design.backend.dto.SavedReportDto;
import com.report_design.backend.model.ReportTemplateEntity;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaReportRepository {

    private final ReportTemplateJpaRepository jpa;
    private final ObjectMapper objectMapper;

    public JpaReportRepository(ReportTemplateJpaRepository jpa, ObjectMapper objectMapper) {
        this.jpa = jpa;
        this.objectMapper = objectMapper;
    }

    public List<ReportListItemDto> findAll() {
        return jpa.findAllByOrderByUpdatedAtDesc().stream()
                .map(e -> new ReportListItemDto(e.getId(), e.getName(), e.getTitle(), e.getUpdatedAt()))
                .toList();
    }

    public Optional<SavedReportDto> findById(String id) {
        return jpa.findById(id).map(this::toDto);
    }

    public SavedReportDto save(ReportDefinitionDto definition, String existingId) {
        Instant now = Instant.now();
        String id = existingId != null ? existingId : UUID.randomUUID().toString();

        ReportTemplateEntity entity = existingId != null
                ? jpa.findById(existingId).orElseGet(ReportTemplateEntity::new)
                : new ReportTemplateEntity();

        entity.setId(id);
        entity.setName(definition.name());
        entity.setTitle(definition.title());
        entity.setDefinitionJson(toJson(definition));
        entity.setUpdatedAt(now);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }

        ReportTemplateEntity saved = jpa.save(entity);
        return toDto(saved);
    }

    public boolean delete(String id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    private SavedReportDto toDto(ReportTemplateEntity e) {
        return new SavedReportDto(
                e.getId(),
                fromJson(e.getDefinitionJson()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private String toJson(ReportDefinitionDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JacksonException e) {
            throw new IllegalStateException("Cannot serialize report definition", e);
        }
    }

    private ReportDefinitionDto fromJson(String json) {
        try {
            return objectMapper.readValue(json, ReportDefinitionDto.class);
        } catch (JacksonException e) {
            throw new IllegalStateException("Cannot deserialize report definition", e);
        }
    }
}
