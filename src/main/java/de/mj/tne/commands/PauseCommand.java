package de.mj.tne.commands;

import de.mj.tne.constants.Emoji;
import de.mj.tne.music.GuildMusicManager;
import de.mj.tne.music.PlayerManager;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.util.List;

public class PauseCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 412303243331371010L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
            return;
        }
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(event.getGuild());

        guildMusicManager.player.setPaused(true);

        event.getChannel().sendMessage(Utils.message(Color.DARK_GRAY, Emoji.PAUSE, "Paused the player!")).queue();
    }

    @Override
    public String getHelp() {
        return "Pause the de.mj.tne.music player";
    }

    @Override
    public String getInvoke() {
        return "pause";
    }
}
