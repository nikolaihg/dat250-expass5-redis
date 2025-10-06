package dat250.poll_backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.Instant;
import java.util.UUID;

public class Vote {
    private UUID id;
    private Instant publishedAt;

    @JsonBackReference("user-votes")
    private User voter;

    private VoteOption votedOn;

    public Vote() {
        this.id = UUID.randomUUID();
        this.publishedAt = Instant.now();
    }

    public Vote(User voter, VoteOption votedOn) {
        this();
        this.voter = voter;
        this.votedOn = votedOn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public VoteOption getVotedOn() {
        return votedOn;
    }

    public void setVotedOn(VoteOption votedOn) {
        this.votedOn = votedOn;
    }
}
