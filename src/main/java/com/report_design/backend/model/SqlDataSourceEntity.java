package com.report_design.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "data_source_sql")
@DiscriminatorValue("sql")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
public class SqlDataSourceEntity extends DataSourceEntity {

    @Column(nullable = false, length = 1024)
    private String jdbcUrl;

    @Column(name = "db_user", length = 255)
    private String user;

    @Column(length = 1024)
    private String password;
}
