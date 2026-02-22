package com.gathering.gathering.service;

import com.gathering.gathering.domain.EventOccurrence;
import com.gathering.gathering.domain.EventTemplate;
import com.gathering.gathering.domain.OccurrenceSubActivity;
import com.gathering.gathering.domain.RecurrenceType;
import com.gathering.gathering.domain.RsvpVote;
import com.gathering.gathering.domain.TemplateSubActivity;
import com.gathering.gathering.dto.CreateTemplateRequest;
import com.gathering.gathering.dto.OccurrenceResponse;
import com.gathering.gathering.dto.OccurrenceSubActivityResponse;
import com.gathering.gathering.dto.SubActivityRequest;
import com.gathering.gathering.dto.TemplateSummaryResponse;
import com.gathering.gathering.dto.VoteResponse;
import com.gathering.gathering.repository.EventOccurrenceRepository;
import com.gathering.gathering.repository.EventTemplateRepository;
import com.gathering.gathering.repository.OccurrenceSubActivityRepository;
import com.gathering.gathering.repository.RsvpVoteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EventService {

    private final EventTemplateRepository eventTemplateRepository;
    private final EventOccurrenceRepository eventOccurrenceRepository;
    private final OccurrenceSubActivityRepository occurrenceSubActivityRepository;
    private final RsvpVoteRepository rsvpVoteRepository;
    private final OccurrenceCodeGenerator occurrenceCodeGenerator;
    private final String defaultTimezone;

    public EventService(
            EventTemplateRepository eventTemplateRepository,
            EventOccurrenceRepository eventOccurrenceRepository,
            OccurrenceSubActivityRepository occurrenceSubActivityRepository,
            RsvpVoteRepository rsvpVoteRepository,
            OccurrenceCodeGenerator occurrenceCodeGenerator,
            @Value("${app.timezone}") String defaultTimezone
    ) {
        this.eventTemplateRepository = eventTemplateRepository;
        this.eventOccurrenceRepository = eventOccurrenceRepository;
        this.occurrenceSubActivityRepository = occurrenceSubActivityRepository;
        this.rsvpVoteRepository = rsvpVoteRepository;
        this.occurrenceCodeGenerator = occurrenceCodeGenerator;
        this.defaultTimezone = defaultTimezone;
    }

    @Transactional
    public TemplateSummaryResponse createTemplate(CreateTemplateRequest request) {
        validateRecurrence(request);

        EventTemplate template = new EventTemplate();
        template.setName(request.name().trim());
        template.setDescription(request.description());
        template.setTimezone(request.timezone() == null || request.timezone().isBlank() ? defaultTimezone : request.timezone());
        template.setRecurrenceType(request.recurrenceType());
        template.setRecurrenceInterval(request.recurrenceInterval());
        template.setWeeklyDay(request.weeklyDay());
        template.setStartDate(request.startDate());
        template.setEndDate(request.endDate());
        template.setStartTime(request.startTime());

        int position = 1;
        for (SubActivityRequest subActivityRequest : request.subActivities()) {
            TemplateSubActivity subActivity = new TemplateSubActivity();
            subActivity.setTemplate(template);
            subActivity.setName(subActivityRequest.name().trim());
            subActivity.setPosition(position++);
            template.getSubActivities().add(subActivity);
        }

        EventTemplate saved = eventTemplateRepository.save(template);
        return toTemplateSummary(saved);
    }

    @Transactional(readOnly = true)
    public List<TemplateSummaryResponse> listTemplates() {
        return eventTemplateRepository.findAll().stream().map(this::toTemplateSummary).toList();
    }

    @Transactional
    public List<OccurrenceResponse> generateOccurrences(Long templateId, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "to must be greater than or equal to from");
        }

        EventTemplate template = eventTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));

        List<OccurrenceResponse> created = new ArrayList<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            if (!template.isActive() || !matches(template, date)) {
                continue;
            }
            if (eventOccurrenceRepository.existsByTemplateIdAndOccurrenceDate(template.getId(), date)) {
                continue;
            }

            EventOccurrence occurrence = new EventOccurrence();
            occurrence.setTemplate(template);
            occurrence.setOccurrenceDate(date);
            occurrence.setJoinCode(newUniqueJoinCode());

            List<TemplateSubActivity> sortedSubActivities = template.getSubActivities().stream()
                    .sorted(Comparator.comparingInt(TemplateSubActivity::getPosition))
                    .toList();

            for (TemplateSubActivity templateSubActivity : sortedSubActivities) {
                OccurrenceSubActivity occurrenceSubActivity = new OccurrenceSubActivity();
                occurrenceSubActivity.setOccurrence(occurrence);
                occurrenceSubActivity.setTemplateSubActivityId(templateSubActivity.getId());
                occurrenceSubActivity.setName(templateSubActivity.getName());
                occurrenceSubActivity.setPosition(templateSubActivity.getPosition());
                occurrence.getSubActivities().add(occurrenceSubActivity);
            }

            EventOccurrence saved = eventOccurrenceRepository.save(occurrence);
            created.add(toOccurrenceResponse(saved));
        }
        return created;
    }

    @Transactional(readOnly = true)
    public List<OccurrenceResponse> listOccurrences(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "to must be greater than or equal to from");
        }
        return eventOccurrenceRepository.findByOccurrenceDateBetweenOrderByOccurrenceDateAsc(from, to).stream()
                .map(this::toOccurrenceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OccurrenceResponse getByJoinCode(String joinCode) {
        EventOccurrence occurrence = eventOccurrenceRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Occurrence not found for join code"));
        return toOccurrenceResponse(occurrence);
    }

    @Transactional(readOnly = true)
    public OccurrenceResponse getOccurrenceById(Long occurrenceId) {
        EventOccurrence occurrence = eventOccurrenceRepository.findById(occurrenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Occurrence not found"));
        return toOccurrenceResponse(occurrence);
    }

    @Transactional
    public VoteResponse addVote(Long occurrenceId, Long subActivityId, String name, String email, boolean yesNo, Boolean emailOptIn) {
        EventOccurrence occurrence = eventOccurrenceRepository.findById(occurrenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Occurrence not found"));

        OccurrenceSubActivity subActivity = occurrenceSubActivityRepository.findById(subActivityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub-activity not found"));

        if (!subActivity.getOccurrence().getId().equals(occurrence.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sub-activity does not belong to this occurrence");
        }

        RsvpVote vote = new RsvpVote();
        vote.setOccurrenceSubActivity(subActivity);
        vote.setParticipantName(name.trim());
        vote.setParticipantEmail(email == null || email.isBlank() ? null : email.trim());
        vote.setYesNo(yesNo);
        vote.setEmailOptIn(emailOptIn != null && emailOptIn);

        RsvpVote saved = rsvpVoteRepository.save(vote);
        return new VoteResponse(saved.getId(), saved.getParticipantName(), saved.getParticipantEmail(), saved.isYesNo(), saved.isEmailOptIn());
    }

    @Transactional
    public void deleteVote(Long voteId) {
        if (!rsvpVoteRepository.existsById(voteId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote not found");
        }
        rsvpVoteRepository.deleteById(voteId);
    }

    private String newUniqueJoinCode() {
        String code = occurrenceCodeGenerator.generate();
        while (eventOccurrenceRepository.existsByJoinCode(code)) {
            code = occurrenceCodeGenerator.generate();
        }
        return code;
    }

    private boolean matches(EventTemplate template, LocalDate date) {
        if (date.isBefore(template.getStartDate())) {
            return false;
        }
        if (template.getEndDate() != null && date.isAfter(template.getEndDate())) {
            return false;
        }

        RecurrenceType type = template.getRecurrenceType();
        return switch (type) {
            case ONCE -> date.equals(template.getStartDate());
            case DAILY -> true;
            case WEEKLY -> template.getWeeklyDay() != null && date.getDayOfWeek() == template.getWeeklyDay();
            case EVERY_X_DAYS -> {
                if (template.getRecurrenceInterval() == null || template.getRecurrenceInterval() <= 0) {
                    yield false;
                }
                long days = ChronoUnit.DAYS.between(template.getStartDate(), date);
                yield days % template.getRecurrenceInterval() == 0;
            }
        };
    }

    private void validateRecurrence(CreateTemplateRequest request) {
        if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate must be greater than or equal to startDate");
        }

        if (request.recurrenceType() == RecurrenceType.WEEKLY && request.weeklyDay() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "weeklyDay is required for WEEKLY recurrence");
        }

        if (request.recurrenceType() == RecurrenceType.EVERY_X_DAYS
                && (request.recurrenceInterval() == null || request.recurrenceInterval() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recurrenceInterval must be >= 1 for EVERY_X_DAYS recurrence");
        }
    }

    private TemplateSummaryResponse toTemplateSummary(EventTemplate template) {
        return new TemplateSummaryResponse(
                template.getId(),
                template.getName(),
                template.getRecurrenceType(),
                template.getStartDate(),
                template.getEndDate(),
                template.getStartTime(),
                template.getTimezone(),
                template.isActive()
        );
    }

    private OccurrenceResponse toOccurrenceResponse(EventOccurrence occurrence) {
        List<OccurrenceSubActivityResponse> subActivities = occurrenceSubActivityRepository
                .findByOccurrenceIdOrderByPositionAsc(occurrence.getId())
                .stream()
                .map(subActivity -> {
                    List<VoteResponse> votes = rsvpVoteRepository.findByOccurrenceSubActivityIdOrderByIdDesc(subActivity.getId())
                            .stream()
                            .map(v -> new VoteResponse(v.getId(), v.getParticipantName(), v.getParticipantEmail(), v.isYesNo(), v.isEmailOptIn()))
                            .toList();
                    long yesCount = votes.stream().filter(VoteResponse::yesNo).count();
                    long noCount = votes.size() - yesCount;
                    return new OccurrenceSubActivityResponse(subActivity.getId(), subActivity.getName(), yesCount, noCount, votes);
                })
                .toList();

        return new OccurrenceResponse(
                occurrence.getId(),
                occurrence.getTemplate().getId(),
                occurrence.getTemplate().getName(),
                occurrence.getOccurrenceDate(),
                occurrence.getTemplate().getStartTime(),
                occurrence.getTemplate().getTimezone(),
                occurrence.getJoinCode(),
                subActivities
        );
    }
}
