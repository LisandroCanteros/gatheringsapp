package com.gathering.gathering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VoteRequest(
        @NotNull Long subActivityId,
        @NotBlank String participantName,
        String participantEmail,
        @NotNull Boolean yesNo,
        Boolean emailOptIn
) {
}
