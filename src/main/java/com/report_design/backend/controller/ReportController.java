package com.report_design.backend.controller;

import com.report_design.backend.dto.RenderRequest;
import com.report_design.backend.dto.exception.UnknownFormatException;
import com.report_design.backend.service.ReportAssembler;
import com.report_design_lib.core.RendererRegistry;
import com.report_design_lib.core.Report;
import com.report_design_lib.core.render.ReportRenderer;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportAssembler assembler;

    public ReportController(ReportAssembler assembler) {
        this.assembler = assembler;
    }

    @PostMapping(
            value = "/render/{format}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<byte[]> render(
            @PathVariable String format,
            @Valid @RequestBody RenderRequest request) {

        Report report = assembler.assemble(request.report());

        ReportRenderer renderer;
        try {
            renderer = RendererRegistry.get(format);
        } catch (IllegalStateException e) {
            throw new UnknownFormatException(format);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.render(report, request.parameters() != null ? request.parameters() : java.util.Map.of(), out);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(renderer.getContentType()));
        headers.setContentDispositionFormData("attachment",
                report.getName() + "." + renderer.getDefaultExtension());

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    @GetMapping("/formats")
    public java.util.List<String> formats() {
        return RendererRegistry.available();
    }
}
