package de.mj.tne.commands;

import de.mj.tne.TNEDiscordBot;
import de.mj.tne.constants.Emoji;
import de.mj.tne.music.GuildMusicManager;
import de.mj.tne.music.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.Data;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PlayCommand implements ICommand {


    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 412303243331371010L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
            return;
        }

        TextChannel textChannel = event.getChannel();

        if (args.isEmpty()) {
            PlayerManager playerManager = PlayerManager.getInstance();
            GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(event.getGuild());
            if (guildMusicManager.player.isPaused()) {
                guildMusicManager.player.setPaused(false);
                textChannel.sendMessage(Utils.message(Color.GREEN, Emoji.MUSIC, "Resumed " + guildMusicManager.player.getPlayingTrack().getInfo().title)).queue();
            }
            else
                textChannel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Nothing to play! Queue is empty!")).queue();
            return;
        }

        String input = String.join(" ", args);

        if (!isUrl(input) && !input.startsWith("ytsearch:")) {
            textChannel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please provide a valid youtube link!")).queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        TNEDiscordBot.audioManager = audioManager;

        if (!audioManager.isConnected()) {
            GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
            if (!memberVoiceState.inVoiceChannel()) {
                textChannel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please join a voice channel first!")).queue();
                return;
            }
            VoiceChannel voiceChannel = memberVoiceState.getChannel();
            TNEDiscordBot.voiceChannel = voiceChannel;
            Member selfMember = event.getGuild().getSelfMember();

            if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                textChannel.sendMessageFormat("I am missing permission to join %s", voiceChannel).queue();
                return;
            }
            audioManager.openAudioConnection(voiceChannel);
            textChannel.sendMessage(Utils.message(Color.GREEN, Emoji.MUSIC, "Joined your voice channel!")).queue();
        }

        PlayerManager playerManager = PlayerManager.getInstance();

        playerManager.loadAndPlay(event.getChannel(), input);
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    @Override
    public String getHelp() {
        return "Plays a song\nUsage: " + Data.COMMAND_PREFIX + getInvoke() + " <song url>";
    }

    @Override
    public String getInvoke() {
        return "play";
    }
}
