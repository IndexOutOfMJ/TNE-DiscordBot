package de.mj.tne.commands;

import de.mj.tne.TNEDiscordBot;
import de.mj.tne.constants.Emoji;
import de.mj.tne.music.GuildMusicManager;
import de.mj.tne.music.PlayerManager;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.util.List;

public class QuitCommand implements ICommand {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 412303243331371010L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
            return;
        }
        AudioManager audioManager = event.getGuild().getAudioManager();
        TNEDiscordBot.audioManager = null;
        TNEDiscordBot.voiceChannel = null;

        if (!audioManager.isConnected()) {
            event.getChannel().sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "I'm not connected to an audio channel!")).queue();
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(event.getGuild());

        guildMusicManager.player.setPaused(true);

        audioManager.closeAudioConnection();
        event.getChannel().sendMessage(Utils.message(Color.GREEN, Emoji.MARK_MINUS_SIGN, "Connection to audio channel closed!")).queue();
    }

    @Override
    public String getHelp() {
        return "Let's the bot disconnect from the audio channel";
    }

    @Override
    public String getInvoke() {
        return "quit";
    }
}
