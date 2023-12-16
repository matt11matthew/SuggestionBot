package me.matthewe.suggestion.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import me.matthewe.suggestion.io.utilities.StringReplacer;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * Created by Matthew E on 12/10/2023 at 3:37 PM for the project TicketBot
 */
public class EmbedValue {
    @JsonProperty("title")
    public String title;

    @JsonProperty("color")
    public int color;
    @JsonProperty("description")
    public String description;


    public EmbedValue() {

    }
    public EmbedValue(String title, int color, String description) {
        this.title = title;
        this.color = color;
        this.description = description;
    }

    public EmbedBuilder toEmbedBuilder(StringReplacer stringReplacer) {
        return new EmbedBuilder().setTitle(stringReplacer.replace(title)).setColor(color).setDescription(stringReplacer.replace(description));

    }

    public EmbedBuilder toEmbedBuilder() {
        return toEmbedBuilder(s -> s);
    }
}
