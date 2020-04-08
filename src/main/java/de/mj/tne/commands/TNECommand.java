package de.mj.tne.commands;

import com.google.api.services.sheets.v4.model.ValueRange;
import de.mj.tne.TNEDiscordBot;
import de.mj.tne.constants.Emoji;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.Utils;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfoCommand implements ICommand {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 693851271316373555L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot-commands only into the #bot-control channel!")).queue();
            return;
        }

        ValueRange result;
        try {
            result = TNEDiscordBot.service.spreadsheets().values().get("1DlLQOnZmX_sX1UwiE2pqdz1ERGw__CZCIfAIgkdWmTs", "Infos!A1:B").execute();
            List<Object> objects = new ArrayList<>(result.getValues());
            //[Infos, ]
            //[Stadskdj, kjadha]
            StringBuilder stringBuilder = new StringBuilder();
            objects.forEach(o -> {
                stringBuilder.append(o.toString().replace("[", "").replace("]", "").replace(",", ":")).append("\n");
            });
            event.getChannel().sendMessage(Utils.message(Color.GRAY, "INFOS", stringBuilder.toString())).complete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        event.getMessage().delete().complete();
    }

    @Override
    public String getHelp() {
        return "Sends information about the TNE-Crew.";
    }

    @Override
    public String getInvoke() {
        return "info";
    }
}
