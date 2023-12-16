package me.matthewe.suggestion;

import java.util.Scanner;

/**
 * Created by Matthew E on 12/10/2023 at 11:54 AM for the project TicketBot
 *
 * This class  exists for handling system processes
 */
public class Main {


    public static void main(String[] args) {
        SuggestionBot suggestionBot = new SuggestionBot();
        suggestionBot.start();

        Thread shutdownHook = new Thread(suggestionBot::handleShutdown);
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        /*
        Handles commands
         */
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String next = scanner.nextLine();
            if (next.isEmpty()) continue;
            suggestionBot.onMessage(next);
        }

    }
}
