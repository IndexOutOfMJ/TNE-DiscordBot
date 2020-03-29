package de.mj.tne.commands;

import de.mj.tne.constants.Emoji;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.MineCraftData;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.util.List;

public class StatusCommand implements ICommand {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 412303243331371010L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
            return;
        }
        TextChannel textChannel = event.getChannel();
        textChannel.sendMessage(Utils.message(Color.GRAY, "PirateMC Server Status Check...", "Minecraft server status of mc.piratemc.com")).queue();

        MineCraftData minecraftData = new MineCraftData("mc.piratemc.com", 25565);
        if (minecraftData.isServerUp())
            textChannel.sendMessage(Utils.message(Color.GREEN, "PirateMC Server Status",
                    ":white_check_mark: Server is online\n"
                    + "with " + minecraftData.getCurrentPlayers() + " of " + minecraftData.getMaximumPlayers() + " players!"
                    + "\n Latency: " + minecraftData.getLatency() + " ms")).queue();
        else
            textChannel.sendMessage(Utils.message(Color.RED, "PirateMC Server Status", ":sos: Server is offline...")).queue();
    }

    @Override
    public String getHelp() {
        return "Shows information about the piratemc server";
    }

    @Override
    public String getInvoke() {
        return "status";
    }
}
