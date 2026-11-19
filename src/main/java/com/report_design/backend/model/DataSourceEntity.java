package com.report_design.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "data_sources")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "provider", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public class DataSourceEntity {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "provider", insertable = false, updatable = false)
    private String provider;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
