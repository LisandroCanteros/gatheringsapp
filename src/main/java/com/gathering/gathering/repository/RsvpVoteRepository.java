package com.gathering.gathering.repository;

import com.gathering.gathering.domain.RsvpVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RsvpVoteRepository extends JpaRepository<RsvpVote, Long> {

    List<RsvpVote> findByOccurrenceSubActivityIdOrderByIdDesc(Long occurrenceSubActivityId);
}
