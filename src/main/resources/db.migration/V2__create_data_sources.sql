-- общая таблица
CREATE TABLE data_sources (
    id          VARCHAR(36) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    provider    VARCHAR(32)  NOT NULL,   -- discriminator
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

CREATE INDEX idx_ds_name ON data_sources (name);

-- SQL-провайдер
CREATE TABLE data_source_sql (
    id          VARCHAR(36) PRIMARY KEY,
    jdbc_url    VARCHAR(1024) NOT NULL,
    db_user     VARCHAR(255),
    password    VARCHAR(1024),
    CONSTRAINT fk_ds_sql FOREIGN KEY (id) REFERENCES data_sources(id) ON DELETE CASCADE
);

-- Web-провайдер
CREATE TABLE data_source_web (
    id          VARCHAR(36) PRIMARY KEY,
    url         VARCHAR(1024) NOT NULL,
    json_path   VARCHAR(512),
    CONSTRAINT fk_ds_web FOREIGN KEY (id) REFERENCES data_sources(id) ON DELETE CASCADE
);

-- Custom-провайдер
CREATE TABLE data_source_custom (
    id              VARCHAR(36) PRIMARY KEY,
    provider_name   VARCHAR(64)  NOT NULL,   -- 'mongodb', 'kafka', ...
    options         TEXT,
    CONSTRAINT fk_ds_custom FOREIGN KEY (id) REFERENCES data_sources(id) ON DELETE CASCADE
);