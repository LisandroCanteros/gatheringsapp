package com.gathering.gathering.repository;

import com.gathering.gathering.domain.OccurrenceSubActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OccurrenceSubActivityRepository extends JpaRepository<OccurrenceSubActivity, Long> {

    List<OccurrenceSubActivity> findByOccurrenceIdOrderByPositionAsc(Long occurrenceId);
}
