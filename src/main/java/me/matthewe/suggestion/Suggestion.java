package me.matthewe.suggestion;

import java.util.UUID;

/**
 * Created by Matthew E on 12/16/2023 at 12:45 PM for the project SuggestionBot
 */
public class Suggestion {
    private UUID id;
    private long messageId;
    private long user;
    private String description;
    private SuggestionStatus status;

    public Suggestion(UUID id, long messageId, long user, String description, SuggestionStatus status) {
        this.id = id;
        this.messageId = messageId;
        this.user = user;
        this.description = description;
        this.status = status;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }

    public Suggestion() {

    }

    public UUID getId() {
        return id;
    }

    public long getMessageId() {
        return messageId;
    }

    public long getUser() {
        return user;
    }

    public String getDescription() {
        return description;
    }

    public SuggestionStatus getStatus() {
        return status;
    }
}
