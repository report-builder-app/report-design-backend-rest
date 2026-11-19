package com.report_design.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "data_source_web")
@DiscriminatorValue("web")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
public class WebDataSourceEntity extends DataSourceEntity {

    @Column(nullable = false, length = 1024)
    private String url;

    @Column(length = 512)
    private String jsonPath;
}
