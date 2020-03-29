package de.mj.tne.commands;

import de.mj.tne.constants.Emoji;
import de.mj.tne.manager.CommandManager;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.util.List;

public class HelpCommand implements ICommand {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 412303243331371010L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        CommandManager.getCommands().forEach((s, iCommand) -> stringBuilder.append("!").append(s).append(": ").append(iCommand.getHelp()).append("\n\n"));
        event.getChannel().sendMessage(Utils.message(Color.ORANGE, ":regional_indicator_q:\n\nCommand Help:", stringBuilder.toString())).queue();
    }

    @Override
    public String getHelp() {
        return "Show's this page";
    }

    @Override
    public String getInvoke() {
        return "help";
    }
}
