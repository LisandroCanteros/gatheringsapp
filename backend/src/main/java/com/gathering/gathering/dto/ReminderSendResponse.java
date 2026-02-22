package com.gathering.gathering.dto;

import java.time.LocalDate;

public record ReminderSendResponse(
        LocalDate date,
        int sentCount
) {
}
