package com.gathering.gathering.repository;

import com.gathering.gathering.domain.RsvpVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RsvpVoteRepositoryCustom extends JpaRepository<RsvpVote, Long> {

    @Query("""
        SELECT v FROM RsvpVote v
        JOIN FETCH v.occurrenceSubActivity osa
        JOIN FETCH osa.occurrence o
        JOIN FETCH o.template t
        WHERE o.occurrenceDate = :targetDate
        AND v.participantEmail IS NOT NULL
        AND v.emailOptIn = true
        AND v.yesNo = true
        """)
    List<RsvpVote> findOptedInYesVotesForDate(@Param("targetDate") LocalDate targetDate);
}
