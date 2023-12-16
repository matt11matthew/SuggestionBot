package me.matthewe.suggestion.io.console.command;

import me.matthewe.suggestion.SuggestionBot;

/**
 * Created by Matthew E on 12/10/2023 at 12:10 PM for the project TicketBot
 */
public abstract class ConsoleCommand {
    private String name;
    private SuggestionBot suggestionBot;

    protected ConsoleCommand(String name, SuggestionBot suggestionBot) {
        this.name = name;
        this.suggestionBot = suggestionBot;
    }

    public String getName() {
        return name;
    }

    public  abstract void onCommand(String[] args);
}
