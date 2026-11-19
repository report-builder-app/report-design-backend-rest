package com.report_design.backend.repository;

import com.report_design.backend.model.ReportTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportTemplateJpaRepository extends JpaRepository<ReportTemplateEntity, String> {
    List<ReportTemplateEntity> findAllByOrderByUpdatedAtDesc();
}
