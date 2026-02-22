package com.gathering.gathering.controller;

import com.gathering.gathering.dto.OccurrenceResponse;
import com.gathering.gathering.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/occurrences")
public class OccurrenceController {

    private final EventService eventService;

    public OccurrenceController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<OccurrenceResponse> listOccurrences(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return eventService.listOccurrences(from, to);
    }

    @GetMapping("/{occurrenceId}")
    public OccurrenceResponse getOccurrence(@PathVariable Long occurrenceId) {
        return eventService.getOccurrenceById(occurrenceId);
    }
}
