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
import de.mj.tne.utils.Utils;


import java.awt.*;
import java.util.List;

public class JoinCommand implements ICommand {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 412303243331371010L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
            return;
        }
        TextChannel textChannel = event.getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        TNEDiscordBot.audioManager = audioManager;

        if (audioManager.isConnected()) {
            textChannel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "I'am already connected to a channel!")).queue();
            return;
        }
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
        textChannel.sendMessage(Utils.message(Color.GREEN, Emoji.MUSIC, "Joining your voice channel!")).queue();

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(event.getGuild());
        if (!guildMusicManager.scheduler.getQueue().isEmpty()) {
            guildMusicManager.player.setPaused(false);
        }
    }

    @Override
    public String getHelp() {
        return "Makes the bot join your channel";
    }

    @Override
    public String getInvoke() {
        return "join";
    }
}
