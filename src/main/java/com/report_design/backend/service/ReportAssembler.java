package com.report_design.backend.service;

import com.report_design.backend.dto.ReportDefinitionDto;
import com.report_design.backend.dto.*;
import com.report_design_lib.core.Report;
import com.report_design_lib.core.data_source.CustomConfig;
import com.report_design_lib.core.data_source.SqlConfig;
import com.report_design_lib.core.data_source.WebConfig;
import com.report_design_lib.core.elements.*;
import com.report_design_lib.core.elements.bands.*;
import com.report_design_lib.interfaces.DataSourceConfig;
import org.springframework.stereotype.Service;

@Service
public class ReportAssembler {

    public Report assemble(ReportDefinitionDto dto) {
        Report report = new Report(dto.name());
        report.setTitle(dto.title());

        if (dto.pageFormat() != null) {
            report.setPageFormat(PageFormat.valueOf(dto.pageFormat()));
        }
        if (dto.orientation() != null) {
            report.setOrientation(PageOrientation.valueOf(dto.orientation()));
        }

        if (dto.parameters() != null) {
            Parameters parameters = new Parameters();
            dto.parameters().forEach(p ->
                    parameters.param(p.name(), resolveType(p.type()), p.defaultValue()));
            report.setParameters(parameters);
        }

        if (dto.fields() != null) {
            Fields fields = new Fields();
            dto.fields().forEach(f ->
                    fields.field(f.name(), resolveType(f.type())));
            report.setFields(fields);
        }

        report.setQueryString(dto.queryString());

        if (dto.bands() != null) {
            Bands bands = new Bands();
            dto.bands().forEach(b -> attachBand(bands, b));
            report.setBands(bands);
        }

        if (dto.dataSource() != null) {
            report.setDataSource(assembleDataSource(dto.dataSource()));
        }

        return report;
    }

    private void attachBand(Bands bands, BandDto dto) {
        BandType type = BandType.valueOf(dto.type());
        Band band = switch (type) {
            case TITLE -> bands.getTitleBand() != null ? bands.getTitleBand() : new TitleBand();
            case COLUMN_HEADER -> bands.getColumnHeader() != null ? bands.getColumnHeader() : new ColumnHeaderBand();
            case DETAIL -> bands.getDetail() != null ? bands.getDetail() : new DetailBand();
            case SUMMARY -> bands.getSummary() != null ? bands.getSummary() : new SummaryBand();
        };
        if (dto.elements() != null) {
            dto.elements().forEach(e -> attachElement(band, e));
        }
        switch (type) {
            case TITLE -> bands.setTitleBand((TitleBand) band);
            case COLUMN_HEADER -> bands.setColumnHeader((ColumnHeaderBand) band);
            case DETAIL -> bands.setDetail((DetailBand) band);
            case SUMMARY -> bands.setSummary((SummaryBand) band);
        }
    }

    private void attachElement(Band band, ElementDto dto) {
        switch (dto.kind()) {
            case "label" -> {
                Label label = new Label();
                label.setText(dto.text());
                label.setStyle(dto.style());
                label.setWidth(dto.width());
                label.setHeight(dto.height());
                band.add(label);
            }
            case "textField" -> {
                TextField field = new TextField();
                field.setExpression(dto.expression());
                field.setStyle(dto.style());
                field.setWidth(dto.width());
                band.add(field);
            }
            case "line" -> {
                Line line = new Line();
                line.setY(dto.y());
                line.setWidth(dto.width());
                band.add(line);
            }
            default -> throw new IllegalArgumentException("Unknown element kind: " + dto.kind());
        }
    }

    private DataSourceConfig assembleDataSource(DataSourceDto dto) {
        return switch (dto.provider()) {
            case "sql" -> {
                SqlConfig cfg = new SqlConfig();
                cfg.setJdbcUrl(dto.jdbcUrl());
                cfg.setUser(dto.user());
                cfg.setPassword(dto.password());
                yield cfg;
            }
            case "web" -> {
                WebConfig cfg = new WebConfig();
                cfg.setUrl(dto.url());
                cfg.setJsonPath(dto.jsonPath());
                yield cfg;
            }
            case "custom" -> {
                CustomConfig cfg = new CustomConfig(dto.options() != null ? dto.options().get("providerName").toString() : "unknown");
                if (dto.options() != null) cfg.setOptions(dto.options());
                yield cfg;
            }
            default -> throw new IllegalArgumentException("Unknown datasource provider: " + dto.provider());
        };
    }

    private Class<?> resolveType(String type) {
        return switch (type) {
            case "String" -> String.class;
            case "Long" -> Long.class;
            case "Integer" -> Integer.class;
            case "Date" -> java.util.Date.class;
            case "BigDecimal" -> java.math.BigDecimal.class;
            default -> String.class;
        };
    }
}
