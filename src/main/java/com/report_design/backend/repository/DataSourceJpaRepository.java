package com.report_design.backend.repository;

import com.report_design.backend.model.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSourceJpaRepository extends JpaRepository<DataSourceEntity, String> {
}
