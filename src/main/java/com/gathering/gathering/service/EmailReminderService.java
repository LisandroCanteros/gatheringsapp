package com.gathering.gathering.service;

import com.gathering.gathering.domain.RsvpVote;
import com.gathering.gathering.repository.RsvpVoteRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailReminderService {

    private static final Logger logger = LoggerFactory.getLogger(EmailReminderService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

    private final RsvpVoteRepositoryCustom rsvpVoteRepository;
    private final JavaMailSender mailSender;
    private final boolean mailEnabled;
    private final String fromAddress;

    public EmailReminderService(
            RsvpVoteRepositoryCustom rsvpVoteRepository,
            JavaMailSender mailSender,
            @Value("${app.mail.enabled}") boolean mailEnabled,
            @Value("${app.mail.from}") String fromAddress
    ) {
        this.rsvpVoteRepository = rsvpVoteRepository;
        this.mailSender = mailSender;
        this.mailEnabled = mailEnabled;
        this.fromAddress = fromAddress;
    }

    @Scheduled(cron = "0 0 9 * * *") // Every day at 9 AM
    public void sendDailyReminders() {
        if (!mailEnabled) {
            logger.info("Email reminders disabled (app.mail.enabled=false)");
            return;
        }

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        sendRemindersForDate(tomorrow);
    }

    public int sendRemindersForDate(LocalDate date) {
        if (!mailEnabled) {
            logger.info("Email reminders disabled (app.mail.enabled=false)");
            return 0;
        }

        List<RsvpVote> votes = rsvpVoteRepository.findOptedInYesVotesForDate(date);

        logger.info("Sending {} reminder(s) for occurrences on {}", votes.size(), date);

        int sentCount = 0;
        for (RsvpVote vote : votes) {
            try {
                sendReminder(vote);
                sentCount++;
            } catch (MailException ex) {
                logger.error("Failed to send reminder to {}: {}", vote.getParticipantEmail(), ex.getMessage());
            }
        }
        return sentCount;
    }

    private void sendReminder(RsvpVote vote) {
        var occurrence = vote.getOccurrenceSubActivity().getOccurrence();
        var template = occurrence.getTemplate();
        String eventName = template.getName();
        String subActivityName = vote.getOccurrenceSubActivity().getName();
        String dateStr = occurrence.getOccurrenceDate().format(DATE_FORMATTER);
        String timeStr = template.getStartTime().toString();

        String subject = String.format("Reminder: %s tomorrow", eventName);
        String body = String.format("""
            Hi %s,
            
            This is a friendly reminder that you voted YES for "%s" in the upcoming event:
            
            📅 Event: %s
            🗓️  Date: %s
            ⏰ Time: %s
            🎯 Activity: %s
            
            See you there!
            
            ---
            Gathering App
            """,
            vote.getParticipantName(),
            subActivityName,
            eventName,
            dateStr,
            timeStr,
            subActivityName
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(vote.getParticipantEmail());
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        logger.info("Sent reminder to {} for {}", vote.getParticipantEmail(), eventName);
    }
}
