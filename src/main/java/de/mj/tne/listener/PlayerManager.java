package de.mj.tne.listener;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import de.mj.tne.utils.Data;
import de.mj.tne.utils.Utils;

import java.awt.*;

public class PlayerManager extends ListenerAdapter {

    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        event.getJDA().getTextChannelById(Data.CHANNEL_WELCOME_GOODBYE).sendMessage(Utils.message(Color.GREEN, "Welcome", "Welcome to the TNE Crew " + event.getMember().getAsMention() + " :wave:")).queue();
    }

    public void onGuildMemberLeave(@NotNull GuildMemberLeaveEvent event) {
        event.getJDA().getTextChannelById(Data.CHANNEL_WELCOME_GOODBYE).sendMessage(Utils.message(Color.RED, "Goodbye", event.getMember().getAsMention() + " has left the TNE Crew! :wave:")).queue();
    }

    public void onGuildMemberNickChange(@NotNull GuildMemberUpdateNicknameEvent event) {
        event.getGuild().getTextChannelById(Data.CHANNEL_INFORMATION).sendMessage(Utils.message(Color.MAGENTA, "Nickname update", ":wrench: " + event.getMember().getAsMention() + " changed his / her nickname to " + event.getNewNickname())).queue();
    }

    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        event.getJDA().getTextChannelById(Data.CHANNEL_INFORMATION).sendMessage(Utils.message(Color.ORANGE, "Role update", ":wrench: " + event.getMember().getAsMention() + " got the role " + event.getRoles().get(0).getName())).queue();
    }

    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        event.getJDA().getTextChannelById(Data.CHANNEL_INFORMATION).sendMessage(Utils.message(Color.ORANGE, "Role update", ":wrench: " + event.getMember().getAsMention() + " has the role " + event.getRoles().get(0).getName() + " been removed!")).queue();
    }

}
