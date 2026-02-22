package com.gathering.gathering.repository;

import com.gathering.gathering.domain.EventOccurrence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventOccurrenceRepository extends JpaRepository<EventOccurrence, Long> {

    boolean existsByTemplateIdAndOccurrenceDate(Long templateId, LocalDate occurrenceDate);

    boolean existsByJoinCode(String joinCode);

    List<EventOccurrence> findByOccurrenceDateBetweenOrderByOccurrenceDateAsc(LocalDate from, LocalDate to);

    Optional<EventOccurrence> findByJoinCode(String joinCode);
}
