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

import java.time.LocalDateTime;

@Entity
@Table(name = "rsvp_vote")
public class RsvpVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occurrence_sub_activity_id", nullable = false)
    private OccurrenceSubActivity occurrenceSubActivity;

    @Column(name = "participant_name", nullable = false, length = 120)
    private String participantName;

    @Column(name = "participant_email", length = 255)
    private String participantEmail;

    @Column(name = "yes_no", nullable = false)
    private boolean yesNo;

    @Column(name = "email_opt_in", nullable = false)
    private boolean emailOptIn = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public OccurrenceSubActivity getOccurrenceSubActivity() {
        return occurrenceSubActivity;
    }

    public void setOccurrenceSubActivity(OccurrenceSubActivity occurrenceSubActivity) {
        this.occurrenceSubActivity = occurrenceSubActivity;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }

    public boolean isYesNo() {
        return yesNo;
    }

    public void setYesNo(boolean yesNo) {
        this.yesNo = yesNo;
    }

    public boolean isEmailOptIn() {
        return emailOptIn;
    }

    public void setEmailOptIn(boolean emailOptIn) {
        this.emailOptIn = emailOptIn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
