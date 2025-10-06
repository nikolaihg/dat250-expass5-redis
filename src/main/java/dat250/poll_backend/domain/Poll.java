package dat250.poll_backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Poll {
    private UUID id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    @JsonBackReference("user-polls")
    private User creator;

    @JsonManagedReference("poll-options")
    private List<VoteOption> voteOptions = new ArrayList<>();

    public Poll() {
        this.id = UUID.randomUUID();
    }

    public Poll(String question, Instant publishedAt, Instant validUntil) {
        this();
        this.question = question;
        this.publishedAt = publishedAt;
        this.validUntil = validUntil;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<VoteOption> getVoteOptions() {
        return voteOptions;
    }

    public void setVoteOptions(List<VoteOption> voteOptions) {
        this.voteOptions = voteOptions;
    }
}
