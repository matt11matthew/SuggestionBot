package me.matthewe.suggestion.io.console.command;

import me.matthewe.suggestion.SuggestionBot;

/**
 * Created by Matthew E on 12/10/2023 at 12:57 PM for the project TicketBot
 */
public class StopConsoleCommand extends ConsoleCommand{

    public StopConsoleCommand(SuggestionBot suggestionBot) {
        super("stop", suggestionBot);
    }

    @Override
    public void onCommand(String[] args) {
        System.exit(0);
    }
}
