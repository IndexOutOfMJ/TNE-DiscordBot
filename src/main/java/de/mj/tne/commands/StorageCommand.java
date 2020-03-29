package de.mj.tne.commands;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Maps;
import de.mj.tne.TNEDiscordBot;
import de.mj.tne.constants.Emoji;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StorageCommand implements ICommand {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 693851271316373555L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
            return;
        }
        ValueRange result;
        try {
            result = TNEDiscordBot.service.spreadsheets().values().get("1DlLQOnZmX_sX1UwiE2pqdz1ERGw__CZCIfAIgkdWmTs", "A1:G").execute();
            List<Object> objects = new ArrayList<>(result.getValues());
            String argString = args.toString().replace("[", "").replace("]", "").replace(",", "").toLowerCase();

            Map<String, List<String>> items = Maps.newHashMap();
            objects.forEach(o -> {
                String[] itemArray = o.toString().split(",");
                if (!items.containsKey(itemArray[5])) {
                    items.put(itemArray[5], new ArrayList<>());
                }
                List<String> locations = items.get(itemArray[5]);
                locations.add(itemArray[6].replace("]", ""));
                items.replace(itemArray[5], locations);
            });

            StringBuilder stringBuilder = new StringBuilder();
            items.forEach((item, where) -> {
                if (item.toLowerCase().equalsIgnoreCase(" " + argString)) {
                    where.forEach(place -> stringBuilder.append(place).append("\n"));
                }
            });
            if (stringBuilder.toString().isEmpty()) {
                event.getChannel().sendMessage(Utils.message(Color.ORANGE, "Item: " + argString, "The item you were looking for could not be found.")).complete();
            }
            event.getChannel().sendMessage(Utils.message(Color.GREEN, "Item: " + argString, stringBuilder.toString())).complete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return "Shows the location of the requested item.";
    }

    @Override
    public String getInvoke() {
        return "storage";
    }
}
