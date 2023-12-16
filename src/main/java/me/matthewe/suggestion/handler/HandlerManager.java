package me.matthewe.suggestion.handler;

import me.matthewe.suggestion.SuggestionBot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew E on 12/10/2023 at 11:55 AM for the project TicketBot
 */
public class HandlerManager {
    private Map<String, Handler> handlerMap;

    private SuggestionBot suggestionBot;


    public HandlerManager(SuggestionBot suggestionBot) {
        this.suggestionBot = suggestionBot;
        this.handlerMap = new HashMap<>();
    }
    public <T extends Handler> T getHandler(Class<T> handlerClass) {

        return (T) handlerMap.get(handlerClass.getSimpleName());
    }


    public <T extends Handler> void registerHandler(T handler) {
        if (handlerMap.containsKey(handler.getClass().getSimpleName())){
            return;
        }
        handlerMap.put(handler.getClass().getSimpleName(), handler);
    }

    public void enableHandlers() {
        this.suggestionBot.getLogger().info("Starting...");
        this.handlerMap.values().forEach(Handler::onEnable);
    }
    public void disableHandlers() {
        this.suggestionBot.getLogger().info("Stopping...");
        this.handlerMap.values().stream().sorted((o1, o2) -> o2.getShutdownPriority()-o1.getShutdownPriority()).forEach(handler -> {
            suggestionBot.getLogger().info("Disabling " + handler.getClass().getSimpleName());
            handler.onDisable();

        });

    }
}
