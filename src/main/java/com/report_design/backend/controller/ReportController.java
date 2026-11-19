package com.report_design.backend.controller;

import com.report_design.backend.dto.RenderRequest;
import com.report_design.backend.dto.ValidationResult;
import com.report_design.backend.dto.exception.InvalidReportException;
import com.report_design.backend.dto.exception.UnknownFormatException;
import com.report_design.backend.service.ReportAssembler;
import com.report_design.backend.service.ReportValidator;
import com.report_design.backend.service.RenderCache;
import com.report_design_lib.core.RendererRegistry;
import com.report_design_lib.core.Report;
import com.report_design_lib.core.render.ReportRenderer;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportAssembler assembler;
    private final ReportValidator validator;
    private final RenderCache renderCache;

    public ReportController(ReportAssembler assembler,
                            ReportValidator validator,
                            RenderCache renderCache) {
        this.assembler = assembler;
        this.validator = validator;
        this.renderCache = renderCache;
    }

    /**
     * Генерация отчёта в выбранном формате.
     *
     * @param format         формат вывода (pdf, xls, html, json)
     * @param disposition    attachment (скачивание) или inline (просмотр в iframe)
     * @param idempotencyKey опциональный ключ для кэширования результата
     * @param request        JSON-описание отчёта + runtime-параметры
     */
    @PostMapping(
            value = "/render/{format}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<byte[]> render(
            @PathVariable String format,
            @RequestParam(name = "disposition", defaultValue = "attachment") String disposition,
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody RenderRequest request) {

        // 1. Валидация до рендера — быстрый отказ с понятной ошибкой
        ValidationResult validation = validator.validate(request.report());
        if (!validation.valid()) {
            throw new InvalidReportException(validation.errors());
        }

        // 2. Проверка кэша по Idempotency-Key
        if (idempotencyKey != null) {
            var cached = renderCache.get(idempotencyKey);
            if (cached != null) {
                return buildResponse(cached.body(), cached.contentType(),
                        cached.filename(), disposition);
            }
        }

        // 3. Сборка Report из DTO
        Report report = assembler.assemble(request.report());

        // 4. Поиск рендерера
        ReportRenderer renderer;
        try {
            renderer = RendererRegistry.get(format);
        } catch (IllegalStateException e) {
            throw new UnknownFormatException(format);
        }

        // 5. Рендеринг
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, Object> params = request.parameters() != null
                ? request.parameters()
                : Map.of();
        renderer.render(report, params, out);

        byte[] body = out.toByteArray();
        String filename = report.getName() + "." + renderer.getDefaultExtension();
        String contentType = renderer.getContentType();

        // 6. Кэшируем результат
        if (idempotencyKey != null) {
            renderCache.put(idempotencyKey, body, contentType, filename);
        }

        // 7. Формируем ответ
        return buildResponse(body, contentType, filename, disposition);
    }

    /**
     * Список доступных форматов рендеринга.
     */
    @GetMapping("/formats")
    public List<String> formats() {
        return RendererRegistry.available();
    }

    // ------------------------------------------------------------------
    // Вспомогательные методы
    // ------------------------------------------------------------------

    private ResponseEntity<byte[]> buildResponse(byte[] body,
                                                 String contentType,
                                                 String filename,
                                                 String disposition) {
        HttpHeaders headers = new HttpHeaders();

        // Content-Type
        headers.setContentType(MediaType.parseMediaType(contentType));

        // Content-Disposition
        ContentDisposition cd = "inline".equalsIgnoreCase(disposition)
                ? ContentDisposition.inline().filename(filename, StandardCharsets.UTF_8).build()
                : ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build();
        headers.setContentDisposition(cd);

        // Дополнительно: CORS-заголовок для доступа из JS (если нужно читать имя файла)
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                HttpHeaders.CONTENT_DISPOSITION);

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }
}
