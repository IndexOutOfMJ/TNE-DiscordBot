package de.mj.tne.commands;

import de.mj.tne.TNEDiscordBot;
import de.mj.tne.constants.Emoji;
import de.mj.tne.music.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.util.List;

public class RadioCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 412303243331371010L) {
        event.getMessage().delete().queue();
        PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
        channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
        return;
    }

        // MDR-JUMP http://avw.mdr.de/streams/284320-0_mp3_high.m3u
        // RSA http://streams.rsa-sachsen.de/rsa-live/mp3-192/streams.rsa-sachsen.de/play.m3u
        // FFN https://player.ffn.de/ffnbraunschweig.m3u

        TextChannel textChannel = event.getChannel();

        if (args.isEmpty()) {
            textChannel.sendMessage(Utils.message(Color.ORANGE, Emoji.INFORMATION, "Please use !radio ffn | rsa | mdr-jump")).queue();
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
            textChannel.sendMessage(Utils.message(Color.GREEN, Emoji.ERROR, "Joined your voice channel!")).queue();
        }

        PlayerManager playerManager = PlayerManager.getInstance();

        if (args.get(0).equalsIgnoreCase("ffn")) {
            playerManager.loadAndPlay(event.getChannel(), "https://player.ffn.de/ffnbraunschweig.m3u");
            textChannel.sendMessage(Utils.message(Color.ORANGE, Emoji.RADIO, "Now playing radio ffn")).queue();
            return;
        }
        if (args.get(0).equalsIgnoreCase("rsa")) {
            playerManager.loadAndPlay(event.getChannel(), "http://streams.rsa-sachsen.de/rsa-live/mp3-192/streams.rsa-sachsen.de/play.m3u");
            textChannel.sendMessage(Utils.message(Color.ORANGE, Emoji.RADIO, "Now playing radio rsa")).queue();
            return;
        }
        if (args.get(0).equalsIgnoreCase("mdr-jump")) {
            playerManager.loadAndPlay(event.getChannel(), "http://avw.mdr.de/streams/284320-0_mp3_high.m3u");
            textChannel.sendMessage(Utils.message(Color.ORANGE, Emoji.RADIO, "Now playing radio mdr-jump")).queue();
            return;
        }
        textChannel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "No stream with this name was found!")).queue();
    }

    @Override
    public String getHelp() {
        return "Start a radio stream (RSA | MDR-JUMP | FFN)";
    }

    @Override
    public String getInvoke() {
        return "radio";
    }
}
