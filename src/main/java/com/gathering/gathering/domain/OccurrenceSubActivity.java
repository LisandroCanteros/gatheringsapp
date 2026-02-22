package com.gathering.gathering.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "occurrence_sub_activity")
public class OccurrenceSubActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occurrence_id", nullable = false)
    private EventOccurrence occurrence;

    @Column(name = "template_sub_activity_id")
    private Long templateSubActivityId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private int position;

    public Long getId() {
        return id;
    }

    public EventOccurrence getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(EventOccurrence occurrence) {
        this.occurrence = occurrence;
    }

    public Long getTemplateSubActivityId() {
        return templateSubActivityId;
    }

    public void setTemplateSubActivityId(Long templateSubActivityId) {
        this.templateSubActivityId = templateSubActivityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
