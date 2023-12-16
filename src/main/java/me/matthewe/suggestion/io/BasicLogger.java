package me.matthewe.suggestion.io;

import me.matthewe.suggestion.SuggestionBot;

/**
 * Created by Matthew E on 12/10/2023 at 12:02 PM for the project TicketBot
 */
public class BasicLogger {
    private SuggestionBot suggestionBot;

    public BasicLogger(SuggestionBot suggestionBot) {
        this.suggestionBot = suggestionBot;
    }

    public void info(String text) {
        System.out.println("[TicketBot] " + text);
    }

    public void printLine(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print("=");
        }
        System.out.println();
    }

    public void error(String text) {
        System.err.println("[TicketBot] " + text);
    }
}
