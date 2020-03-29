package de.mj.tne.listener;

import de.mj.tne.games.CounterGame;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import de.mj.tne.utils.Data;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class MessageListener extends ListenerAdapter {

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
        } else {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContentDisplay());
        }
        if (event.getAuthor().isBot())
            return;
        if (event.getMessage().getContentDisplay().equalsIgnoreCase("!info")) {
            StringBuilder out = new StringBuilder(":information_source:");
            String creationDate = event.getGuild().getOwner().getTimeJoined().format(DateTimeFormatter.ofPattern(Data.DATE_PATTERN));
            out
                    .append("\nServerName: ").append(event.getGuild().getName()).append("\n")
                    .append("\nServerId: ").append(event.getGuild().getId()).append("\n")
                    .append("\nMembers: ").append(event.getGuild().getJDA().getUsers().size()).append("\n")
                    .append("\nCreator: ").append(event.getGuild().getOwner().getUser().getName()).append("\n")
                    .append("\nCreation day: ").append(creationDate).append("\n")
                    .append("\nYou joined: ").append(event.getMember().getTimeJoined().format(DateTimeFormatter.ofPattern(Data.DATE_PATTERN)))
                    .append("\nThe bot was made with :heart: by ιη∂єχσυтσƒмנ")
                    .append("\n____________________\n");
            event.getTextChannel().sendMessage(Utils.message(Color.CYAN, "Bot Information", out.toString())).queue();
        }
        if (!event.isFromType(ChannelType.TEXT))
            return;
        if (event.getChannel().getIdLong() == 639840752687644683L) {
            CounterGame.countUpdate(event.getTextChannel(), event.getMessage());
        }
    }
}
