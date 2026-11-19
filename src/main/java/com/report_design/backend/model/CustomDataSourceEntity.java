package com.report_design.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "data_source_custom")
@DiscriminatorValue("custom")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
public class CustomDataSourceEntity extends DataSourceEntity {

    @Column(name = "provider_name", nullable = false, length = 64)
    private String providerName;   // 'mongodb', 'kafka', ...

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> options;
}
