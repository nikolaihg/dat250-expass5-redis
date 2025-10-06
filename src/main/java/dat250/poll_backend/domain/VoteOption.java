package dat250.poll_backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.UUID;

public class VoteOption {
    private UUID id;
    private String caption;
    private int presentationOrder;

    @JsonBackReference("poll-options")
    private Poll poll;

    public VoteOption() {
        this.id = UUID.randomUUID();
    }

    public VoteOption(String caption, int presentationOrder) {
        this();
        this.caption = caption;
        this.presentationOrder = presentationOrder;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getPresentationOrder() {
        return presentationOrder;
    }

    public void setPresentationOrder(int presentationOrder) {
        this.presentationOrder = presentationOrder;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}
