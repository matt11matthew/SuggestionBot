package me.matthewe.suggestion.discord;

import me.matthewe.suggestion.Suggestion;
import me.matthewe.suggestion.SuggestionBot;
import me.matthewe.suggestion.SuggestionStatus;
import me.matthewe.suggestion.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

/**
 * Created by Matthew E on 12/10/2023 at 2:51 PM for the project TicketBot
 */
public class DiscordListener  extends ListenerAdapter {

    private DiscordHandler discordHandler;
    private SuggestionBot suggestionBot;
    private Config config;

    private Map<Integer, Suggestion> suggestionMap;



    public DiscordListener(DiscordHandler discordHandler, SuggestionBot suggestionBot, Config config) {
        this.discordHandler = discordHandler;
        this.suggestionBot = suggestionBot;
        this.config = config;

    }

    @Override
    public void onReady(ReadyEvent event) {
        this.discordHandler.setReady(true);
        this.suggestionMap = new HashMap<>();
        for (Suggestion suggestion : this.discordHandler.getDatabaseHandler().downloadSuggestionsFromDatabase()) {
            suggestionMap.put(suggestion.getId(),suggestion);
        }


    }

    public void onShutdown() {
        List<Suggestion> suggestions = new ArrayList<>();
        suggestions.addAll(suggestionMap.values());

        this.discordHandler.getDatabaseHandler().uploadAllSuggestionsToDatabase(suggestions);
        suggestionMap.clear();
    }








    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (event.getMember().getUser().isBot()) return;
        if (discordHandler.isReady()){
            if (event.getMessage().getChannel().getIdLong()==config.discord.channels.suggestions){
                event.getMessage().delete().queue();
            }
        }

    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        if (event.getModalId().equals("suggestion")) {
            ModalMapping body = event.getInteraction().getValue("body");
            createSuggestion(event.getUser().getAsMention(), event.getUser().getIdLong(), body.getAsString());
            event.reply("Thanks for your request!").setEphemeral(true).queue();
        }
    }



    private void createSuggestion(String mention, long idLong, String asString) {
        TextChannel textChannelById = this.discordHandler.getGuild().getTextChannelById(this.config.discord.channels.suggestions);

        int id = suggestionMap.values().size()+1;

        Suggestion suggestion = new Suggestion(id, 0, idLong, asString, SuggestionStatus.PENDING);

        textChannelById.sendMessage("@everyone").queue(message ->message.delete().queue());
        Message complete = textChannelById.sendMessageEmbeds(new EmbedBuilder().setTitle("New Suggestion " + suggestion.getId()).setColor(Color.WHITE).setFooter("By " + mention).setDescription(asString).build()).complete();

        System.out.println(complete.getIdLong());
        suggestion.setMessageId(complete.getIdLong());

        this.discordHandler.getDatabaseHandler().createSuggestionDatabase(suggestion);

        suggestionMap.put(suggestion.getId(), suggestion);
        System.out.println("Created suggestion");

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("suggestions")) {
//            createTicketChannel(event.getGuild(), event.getMember());
            event.replyModal(Modal.create("suggestion", "Suggestion").setTitle("Suggestion").addActionRow(TextInput.create("body", "Description", TextInputStyle.PARAGRAPH).build()).build()).queue();
            return;
        } else    if (event.getName().equals("suggestion")) {
            String subcommandName = event.getInteraction().getSubcommandName().toLowerCase();
            switch (subcommandName){
                case "deny":
                    OptionMapping suggestionId3 = event.getInteraction().getOption("suggestion_id3");
                    System.out.println(suggestionId3.getAsString());
                    int id = suggestionId3.getAsInt();
                    if (suggestionMap.containsKey(id)){

                        event.reply("Deny " + event.getInteraction().getOption("suggestion_id3").getAsString()).queue();
                        Suggestion suggestion = suggestionMap.get(id);
                        suggestion.setStatus(SuggestionStatus.DENY);
                        this.discordHandler.getDatabaseHandler().updateSuggestionInDatabase(suggestion);
                        TextChannel textChannelById = this.discordHandler.getGuild().getTextChannelById(this.config.discord.channels.suggestions);
                        textChannelById.retrieveMessageById(suggestion.getMessageId()).queue(message -> {
                            message                        .editMessageEmbeds(new EmbedBuilder().setTitle("Suggestion Denied " + suggestion.getId()).setColor(Color.RED).setFooter("Deny!").setDescription(suggestion.getDescription()).build()).queue();
                        }, throwable -> {});
                    } else {
                        event.reply("Please enter a valid ID!").queue();
                    }
                    break;
                case "implement":
                    OptionMapping suggestionId2 = event.getInteraction().getOption("suggestion_id2");
                    System.out.println(suggestionId2.getAsString());
                     id = suggestionId2.getAsInt();
                    if (suggestionMap.containsKey(id)){
                        event.reply("Implemented " + event.getInteraction().getOption("suggestion_id2").getAsInt()).queue();
                        Suggestion suggestion = suggestionMap.get(id);
                        suggestion.setStatus(SuggestionStatus.IMPLEMENTED);
                        this.discordHandler.getDatabaseHandler().updateSuggestionInDatabase(suggestion);
                        TextChannel textChannelById = this.discordHandler.getGuild().getTextChannelById(this.config.discord.channels.suggestions);
                        textChannelById.retrieveMessageById(suggestion.getMessageId()).queue(message -> {
                            message                        .editMessageEmbeds(new EmbedBuilder().setTitle("Suggestion Implemented " + suggestion.getId()).setColor(Color.YELLOW).setFooter("Implemented").setDescription(suggestion.getDescription()).build()).queue();
                        }, throwable -> {});
                    } else {
                        event.reply("Please enter a valid ID!").queue();
                    }
                    break;
                case "accept":

                    OptionMapping suggestionId = event.getInteraction().getOption("suggestion_id");
                    System.out.println(suggestionId.getAsString());
                    id = suggestionId.getAsInt();
                    if (suggestionMap.containsKey(id)){
                        event.reply("Accepted " + event.getInteraction().getOption("suggestion_id").getAsString()).queue();
                        Suggestion suggestion = suggestionMap.get(id);
                        suggestion.setStatus(SuggestionStatus.ACCEPTED);
                        this.discordHandler.getDatabaseHandler().updateSuggestionInDatabase(suggestion);
                        TextChannel textChannelById = this.discordHandler.getGuild().getTextChannelById(this.config.discord.channels.suggestions);
                        textChannelById.retrieveMessageById(suggestion.getMessageId()).queue(message -> {
                            message                        .editMessageEmbeds(new EmbedBuilder().setTitle("Suggestion Accepted " + suggestion.getId()).setColor(Color.GREEN).setFooter("Accepted").setDescription(suggestion.getDescription()).build()).queue();
                        }, throwable -> {});
                    } else {
                        event.reply("Please enter a valid ID!").queue();
                    }
                    break;
            }
////            createTicketChannel(event.getGuild(), event.getMember());
//            event.replyModal(Modal.create("suggestion", "Suggestion").setTitle("Suggestion").addActionRow(TextInput.create("body", "Description", TextInputStyle.PARAGRAPH).build()).build()).complete();
//            return;
        }

    }






}