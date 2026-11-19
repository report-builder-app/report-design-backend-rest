package com.report_design.backend.controller;

import com.report_design.backend.dto.StyleDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class StyleController {

    @GetMapping("/styles")
    public List<StyleDto> styles() {
        return List.of(
                new StyleDto("title", "Helvetica", 18, true, false, "#000000", null),
                new StyleDto("header", "Helvetica", 12, true, false, "#000000", "#EEEEEE"),
                new StyleDto("body", "Helvetica", 10, false, false, "#000000", null),
                new StyleDto("total", "Helvetica", 12, true, false, "#000000", null)
        );
    }
}
