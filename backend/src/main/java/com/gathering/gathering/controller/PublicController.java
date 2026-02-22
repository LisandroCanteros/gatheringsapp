package com.gathering.gathering.controller;

import com.gathering.gathering.dto.OccurrenceResponse;
import com.gathering.gathering.dto.VoteRequest;
import com.gathering.gathering.dto.VoteResponse;
import com.gathering.gathering.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final EventService eventService;

    public PublicController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/join/{joinCode}")
    public OccurrenceResponse join(@PathVariable String joinCode) {
        return eventService.getByJoinCode(joinCode);
    }

    @PostMapping("/occurrences/{occurrenceId}/votes")
    public VoteResponse vote(@PathVariable Long occurrenceId, @Valid @RequestBody VoteRequest request) {
        return eventService.addVote(
                occurrenceId,
                request.subActivityId(),
                request.participantName(),
                request.participantEmail(),
                request.yesNo(),
                request.emailOptIn()
        );
    }

    @DeleteMapping("/votes/{voteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVote(@PathVariable Long voteId) {
        eventService.deleteVote(voteId);
    }
}
