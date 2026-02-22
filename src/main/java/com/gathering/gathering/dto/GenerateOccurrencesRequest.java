package com.gathering.gathering.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GenerateOccurrencesRequest(
        @NotNull LocalDate from,
        @NotNull LocalDate to
) {
}
