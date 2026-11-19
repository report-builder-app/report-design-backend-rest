package com.report_design.backend.service;

import com.report_design.backend.dto.DataSourceDto;
import com.report_design.backend.dto.DataSourceRequest;
import com.report_design.backend.dto.DataSourceResponse;
import com.report_design.backend.dto.DataSourceTestResult;
import com.report_design.backend.dto.exception.DataSourceNotFoundException;
import com.report_design.backend.model.CustomDataSourceEntity;
import com.report_design.backend.model.DataSourceEntity;
import com.report_design.backend.model.SqlDataSourceEntity;
import com.report_design.backend.model.WebDataSourceEntity;
import com.report_design.backend.repository.DataSourceJpaRepository;
import com.report_design_lib.core.data_source.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataSourceService {
    private final DataSourceTester tester;
    private final DataSourceJpaRepository repository;

    public List<DataSourceResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<DataSourceResponse> findById(String id) {
        return repository.findById(id).map(this::toResponse);
    }

    public DataSourceResponse create(DataSourceRequest req) {
        Instant now = Instant.now();
        DataSourceEntity entity = newEntity(req.provider());
        entity.setId(UUID.randomUUID().toString());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        apply(entity, req);
        return toResponse(repository.save(entity));
    }

    public Optional<DataSourceResponse> update(String id, DataSourceRequest req) {
        return repository.findById(id).map(entity -> {
            if (!providerOf(entity).equals(req.provider())) {
                throw new IllegalArgumentException(
                        "Cannot change provider from " + providerOf(entity) + " to " + req.provider());
            }
            apply(entity, req);
            entity.setUpdatedAt(Instant.now());
            return toResponse(repository.save(entity));
        });
    }

    public boolean delete(String id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public DataSourceTestResult test(String id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new DataSourceNotFoundException(id));
        DataSourceDto dto = toDto(entity);
        return tester.test(dto);
    }

    // ---------- преобразование сущности в DTO ----------
    private DataSourceDto toDto(DataSourceEntity entity) {
        if (entity instanceof SqlDataSourceEntity sql) {
            return new DataSourceDto(
                    "sql",
                    sql.getJdbcUrl(),
                    sql.getUser(),
                    sql.getPassword(),
                    null,
                    null,
                    null
            );
        }
        if (entity instanceof WebDataSourceEntity web) {
            return new DataSourceDto(
                    "web",
                    null,
                    null,
                    null,
                    web.getUrl(),
                    web.getJsonPath(),
                    null
            );
        }
        if (entity instanceof CustomDataSourceEntity custom) {
            java.util.Map<String, Object> options = custom.getOptions() != null
                    ? new java.util.HashMap<>(custom.getOptions())
                    : new java.util.HashMap<>();
            options.put("providerName", custom.getProviderName());
            return new DataSourceDto(
                    "custom",
                    null,
                    null,
                    null,
                    null,
                    null,
                    options
            );
        }
        throw new IllegalStateException("Unknown entity type: " + entity.getClass());
    }

    // ---------- создание правильного подкласса ----------
    private DataSourceEntity newEntity(String provider) {
        return switch (provider) {
            case "sql" -> new SqlDataSourceEntity();
            case "web" -> new WebDataSourceEntity();
            case "custom" -> new CustomDataSourceEntity();
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }

    // ---------- apply ----------
    private void apply(DataSourceEntity entity, DataSourceRequest req) {
        entity.setName(req.name());
        if (entity instanceof SqlDataSourceEntity sql) {
            sql.setJdbcUrl(req.jdbcUrl());
            sql.setUser(req.user());
            if (req.password() != null) sql.setPassword(req.password());
        } else if (entity instanceof WebDataSourceEntity web) {
            web.setUrl(req.url());
            web.setJsonPath(req.jsonPath());
        } else if (entity instanceof CustomDataSourceEntity custom) {
            Object pn = req.options() != null ? req.options().get("providerName") : null;
            if (pn == null) throw new IllegalArgumentException("options.providerName is required");
            custom.setProviderName(pn.toString());
            custom.setOptions(req.options());
        }
    }

    // ---------- toResponse ----------
    private DataSourceResponse toResponse(DataSourceEntity e) {
        if (e instanceof SqlDataSourceEntity sql) {
            return new DataSourceResponse(e.getId(), e.getName(), "sql",
                    sql.getJdbcUrl(), sql.getUser(), null, null, null,
                    e.getCreatedAt(), e.getUpdatedAt());
        }
        if (e instanceof WebDataSourceEntity web) {
            return new DataSourceResponse(e.getId(), e.getName(), "web",
                    null, null, web.getUrl(), web.getJsonPath(), null,
                    e.getCreatedAt(), e.getUpdatedAt());
        }
        if (e instanceof CustomDataSourceEntity custom) {
            return new DataSourceResponse(e.getId(), e.getName(), "custom",
                    null, null, null, null, custom.getOptions(),
                    e.getCreatedAt(), e.getUpdatedAt());
        }
        throw new IllegalStateException("Unknown entity: " + e.getClass());
    }

    private String providerOf(DataSourceEntity e) {
        if (e instanceof SqlDataSourceEntity) return "sql";
        if (e instanceof WebDataSourceEntity) return "web";
        if (e instanceof CustomDataSourceEntity) return "custom";
        throw new IllegalStateException("Unknown entity: " + e.getClass());
    }
}
