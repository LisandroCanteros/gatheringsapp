package com.gathering.gathering.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record OccurrenceResponse(
        Long id,
        Long templateId,
        String templateName,
        LocalDate occurrenceDate,
        LocalTime startTime,
        String timezone,
        String joinCode,
        List<OccurrenceSubActivityResponse> subActivities
) {
}
