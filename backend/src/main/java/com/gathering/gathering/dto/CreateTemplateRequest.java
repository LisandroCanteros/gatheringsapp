package com.gathering.gathering.dto;

import com.gathering.gathering.domain.RecurrenceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateTemplateRequest(
        @NotBlank String name,
        String description,
        String timezone,
        @NotNull RecurrenceType recurrenceType,
        Integer recurrenceInterval,
        DayOfWeek weeklyDay,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        @NotNull LocalTime startTime,
        @NotEmpty @Valid List<SubActivityRequest> subActivities
) {
}
