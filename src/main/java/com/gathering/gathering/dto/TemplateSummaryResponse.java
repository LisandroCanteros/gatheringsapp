package com.gathering.gathering.dto;

import com.gathering.gathering.domain.RecurrenceType;

import java.time.LocalDate;
import java.time.LocalTime;

public record TemplateSummaryResponse(
        Long id,
        String name,
        RecurrenceType recurrenceType,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        String timezone,
        boolean active
) {
}
