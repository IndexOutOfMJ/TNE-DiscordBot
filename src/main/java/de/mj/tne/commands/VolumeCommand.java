package de.mj.tne.commands;

import de.mj.tne.constants.Emoji;
import de.mj.tne.music.PlayerManager;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.util.List;

public class VolumeCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 412303243331371010L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
            return;
        }
        PlayerManager playerManager = PlayerManager.getInstance();
        if (args.isEmpty()) {
            event.getChannel().sendMessage(Utils.message(Color.GREEN, Emoji.VOLUME_HIGH, "Current volume: " + playerManager.getGuildMusicManager(event.getGuild()).player.getVolume() + "%")).queue();
            return;
        }

        int volume = Integer.parseInt(args.get(0));
        playerManager.getGuildMusicManager(event.getGuild()).player.setVolume(volume);
        event.getChannel().sendMessage(Utils.message(Color.GREEN, Emoji.VOLUME_HIGH, "Changed volume to " + volume)).queue();
    }

    @Override
    public String getHelp() {
        return "Change the volume of the bot! \n Usage: !volume <number>";
    }

    @Override
    public String getInvoke() {
        return "volume";
    }
}
