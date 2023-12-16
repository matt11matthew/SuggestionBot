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

    private Map<UUID, Suggestion> suggestionMap;



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

    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        if (event.getModalId().equals("suggestion")) {
            ModalMapping body = event.getInteraction().getValue("body");
            createSuggestion(event.getUser().getIdLong(), body.getAsString());
            event.reply("Thanks for your request!").setEphemeral(true).queue();
        }
    }


    private void createSuggestion(long idLong, String asString) {
        TextChannel textChannelById = this.discordHandler.getGuild().getTextChannelById(this.config.discord.channels.suggestions);

        Suggestion suggestion = new Suggestion(UUID.randomUUID(), 0, idLong, asString, SuggestionStatus.PENDING);
        Message complete = textChannelById.sendMessageEmbeds(new EmbedBuilder().setTitle("New Suggestion " + suggestion.getId().toString()).setColor(Color.WHITE).setFooter("By " + this.discordHandler.getGuild().getMemberById(idLong).getAsMention()).setDescription(asString).build()).complete();

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
                    UUID uuid = UUID.fromString(suggestionId3.getAsString());
                    if (uuid!=null&&suggestionMap.containsKey(uuid)){
                        event.reply("Deny " + event.getInteraction().getOption("suggestion_id3").getAsString()).queue();
                        Suggestion suggestion = suggestionMap.get(uuid);
                        suggestion.setStatus(SuggestionStatus.DENY);
                        this.discordHandler.getDatabaseHandler().updateSuggestionInDatabase(suggestion);
                        TextChannel textChannelById = this.discordHandler.getGuild().getTextChannelById(this.config.discord.channels.suggestions);
                        textChannelById.retrieveMessageById(suggestion.getMessageId()).queue(message -> {
                            message                        .editMessageEmbeds(new EmbedBuilder().setTitle("Suggestion Denied " + suggestion.getId().toString()).setColor(Color.RED).setFooter("Deny!").setDescription(suggestion.getDescription()).build()).queue();
                        }, throwable -> {});
                    } else {
                        event.reply("Please enter a valid ID!").queue();
                    }
                    break;
                case "implement":
                    OptionMapping suggestionId2 = event.getInteraction().getOption("suggestion_id2");
                     uuid = UUID.fromString(suggestionId2.getAsString());
                    if (uuid!=null&&suggestionMap.containsKey(uuid)){
                        event.reply("Implemented " + event.getInteraction().getOption("suggestion_id2").getAsString()).queue();
                        Suggestion suggestion = suggestionMap.get(uuid);
                        suggestion.setStatus(SuggestionStatus.IMPLEMENTED);
                        this.discordHandler.getDatabaseHandler().updateSuggestionInDatabase(suggestion);
                        TextChannel textChannelById = this.discordHandler.getGuild().getTextChannelById(this.config.discord.channels.suggestions);
                        textChannelById.retrieveMessageById(suggestion.getMessageId()).queue(message -> {
                            message                        .editMessageEmbeds(new EmbedBuilder().setTitle("Suggestion Implemented " + suggestion.getId().toString()).setColor(Color.YELLOW).setFooter("Implemented").setDescription(suggestion.getDescription()).build()).queue();
                        }, throwable -> {});
                    } else {
                        event.reply("Please enter a valid ID!").queue();
                    }
                    break;
                case "accept":

                    OptionMapping suggestionId = event.getInteraction().getOption("suggestion_id");
                    uuid = UUID.fromString(suggestionId.getAsString());
                    if (uuid!=null&&suggestionMap.containsKey(uuid)){
                        event.reply("Accepted " + event.getInteraction().getOption("suggestion_id").getAsString()).queue();
                        Suggestion suggestion = suggestionMap.get(uuid);
                        suggestion.setStatus(SuggestionStatus.ACCEPTED);
                        this.discordHandler.getDatabaseHandler().updateSuggestionInDatabase(suggestion);
                        TextChannel textChannelById = this.discordHandler.getGuild().getTextChannelById(this.config.discord.channels.suggestions);
                        textChannelById.retrieveMessageById(suggestion.getMessageId()).queue(message -> {
                            message                        .editMessageEmbeds(new EmbedBuilder().setTitle("Suggestion Accepted " + suggestion.getId().toString()).setColor(Color.GREEN).setFooter("Accepted").setDescription(suggestion.getDescription()).build()).queue();
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