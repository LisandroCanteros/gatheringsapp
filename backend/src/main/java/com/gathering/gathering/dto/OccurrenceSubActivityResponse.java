package com.gathering.gathering.dto;

import java.util.List;

public record OccurrenceSubActivityResponse(
        Long id,
        String name,
        long yesCount,
        long noCount,
        List<VoteResponse> votes
) {
}
