package de.mj.tne.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(TextChannel textChannel, String trackUrl) {
        GuildMusicManager musicManager = getGuildMusicManager(textChannel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                textChannel.sendMessage(Utils.message(Color.GREEN, ":notes:", "Adding to queue " + audioTrack.getInfo().title)).queue();

                play(musicManager, audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if (firstTrack == null)
                    firstTrack = audioPlaylist.getTracks().get(0);

                textChannel.sendMessage(Utils.message(Color.GREEN, ":notes:", "Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + audioPlaylist.getName() + ")")).queue();

                play(musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage(Utils.message(Color.ORANGE, ":interrobang:", "Nothing found by " + trackUrl)).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                textChannel.sendMessage(Utils.message(Color.RED, ":x:", "Could not play: " + e.getMessage())).queue();
            }
        });
    }

    private void play(GuildMusicManager musicManager, AudioTrack audioTrack) {
        musicManager.scheduler.queue(audioTrack);
    }


    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
