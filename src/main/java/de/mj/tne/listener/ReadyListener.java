package de.mj.tne.listener;

import de.mj.tne.TNEDiscordBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import de.mj.tne.utils.Data;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ReadyListener extends ListenerAdapter {

    public void onReady(@NotNull ReadyEvent event) {

        StringBuilder out = new StringBuilder(":information_source:");

        for (Guild guild : event.getJDA().getGuilds()) {
            guild.getTextChannelById(Data.CHANNEL_INFORMATION).sendMessage(Utils.message(Color.GREEN, "Bot status update", ":white_check_mark: [TNE] Bot is now online!")).queue();
            String creationDate = guild.getOwner().getTimeJoined().format(DateTimeFormatter.ofPattern(Data.DATE_PATTERN));
            out
                    .append("\nServerName: ")
                    .append(guild.getName()).append("\n")
                    .append("\nServerId: ")
                    .append(guild.getId()).append("\n")
                    .append("\nMembers: ")
                    .append(guild.getJDA().getUsers().size()).append("\n")
                    .append("\nCreator: ")
                    .append(guild.getOwner().getUser().getName()).append("\n")
                    .append("\nCreation day: ")
                    .append(creationDate).append("\n")
                    .append("\nThis bot was made with :heart: by ιη∂єχσυтσƒмנ")
                    .append("\n____________________\n");
        }

        TNEDiscordBot.jda.getGuilds().get(0).getTextChannelById(Data.CHANNEL_BOT_CONTROL).sendMessage(Utils.message(Color.CYAN, "Bot Information: ", out.toString())
        ).queue();
    }
}
