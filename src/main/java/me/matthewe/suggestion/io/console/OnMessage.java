package me.matthewe.suggestion.io.console;

/**
 * Created by Matthew E on 12/10/2023 at 12:06 PM for the project TicketBot
 */
@FunctionalInterface
public interface OnMessage {
    void onMessage(String text);
}
