package com.gathering.gathering.controller;

import com.gathering.gathering.dto.ReminderSendResponse;
import com.gathering.gathering.service.EmailReminderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/reminders")
public class ReminderController {

    private final EmailReminderService emailReminderService;

    public ReminderController(EmailReminderService emailReminderService) {
        this.emailReminderService = emailReminderService;
    }

    @PostMapping("/send")
    public ReminderSendResponse sendReminders(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date == null ? LocalDate.now().plusDays(1) : date;
        int sent = emailReminderService.sendRemindersForDate(targetDate);
        return new ReminderSendResponse(targetDate, sent);
    }
}
