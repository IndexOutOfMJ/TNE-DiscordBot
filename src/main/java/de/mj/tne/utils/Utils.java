package de.mj.tne.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class Utils
{
    public static MessageEmbed warn(String title, String message) {
        return message(Color.ORANGE, title, message);
    }

    public static MessageEmbed error( String title, String message) {
        return message(Color.RED, title, message);
    }

    public static MessageEmbed message(Color color, String title, String messages) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (title != null) {
            embedBuilder.setTitle(title);
        }
        embedBuilder.setColor(color);
        embedBuilder.setDescription(messages);
        return embedBuilder.build();
    }
}