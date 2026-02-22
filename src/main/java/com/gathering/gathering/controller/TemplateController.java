package com.gathering.gathering.controller;

import com.gathering.gathering.dto.CreateTemplateRequest;
import com.gathering.gathering.dto.GenerateOccurrencesRequest;
import com.gathering.gathering.dto.OccurrenceResponse;
import com.gathering.gathering.dto.TemplateSummaryResponse;
import com.gathering.gathering.service.EventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final EventService eventService;

    public TemplateController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public TemplateSummaryResponse createTemplate(@Valid @RequestBody CreateTemplateRequest request) {
        return eventService.createTemplate(request);
    }

    @GetMapping
    public List<TemplateSummaryResponse> listTemplates() {
        return eventService.listTemplates();
    }

    @PostMapping("/{templateId}/occurrences/generate")
    public List<OccurrenceResponse> generateOccurrences(
            @PathVariable Long templateId,
            @Valid @RequestBody GenerateOccurrencesRequest request
    ) {
        return eventService.generateOccurrences(templateId, request.from(), request.to());
    }
}
