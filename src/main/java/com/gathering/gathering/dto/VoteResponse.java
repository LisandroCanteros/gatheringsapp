package com.gathering.gathering.dto;

public record VoteResponse(
        Long id,
        String participantName,
        String participantEmail,
        boolean yesNo,
        boolean emailOptIn
) {
}
