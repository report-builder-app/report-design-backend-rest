package com.report_design.backend.configuration;

import com.report_design_lib.core.RendererRegistry;
import com.report_design_lib.renderer.pdf.PdfRenderer;
import com.report_design_lib.renderer.xls.XlsRenderer;
import com.report_design_lib.renderer.html.HtmlRenderer;
import com.report_design_lib.renderer.json.JsonRenderer;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class RendererConfig {

    @PostConstruct
    void registerRenderers() {
        if (!RendererRegistry.available().contains("pdf")) {
            RendererRegistry.register(new PdfRenderer());
        }
        if (!RendererRegistry.available().contains("xls")) {
            RendererRegistry.register(new XlsRenderer());
        }
        if (!RendererRegistry.available().contains("html")) {
            RendererRegistry.register(new HtmlRenderer());
        }
        if (!RendererRegistry.available().contains("json")) {
            RendererRegistry.register(new JsonRenderer());
        }
    }
}
