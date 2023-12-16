package me.matthewe.suggestion.handler;

import me.matthewe.suggestion.SuggestionBot;
import me.matthewe.suggestion.config.Config;

/**
 * Created by Matthew E on 12/10/2023 at 11:54 AM for the project TicketBot
 */
public abstract class Handler {
    protected SuggestionBot suggestionBot;
    protected Config config;

    protected int shutdownPriority;

    public Handler(SuggestionBot suggestionBot, Config config) {
        this.suggestionBot = suggestionBot;
        this.config = config;
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public int getShutdownPriority() {
        return shutdownPriority;
    }

}
