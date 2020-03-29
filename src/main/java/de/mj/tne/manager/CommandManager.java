package de.mj.tne.manager;

import de.mj.tne.commands.*;
import de.mj.tne.constants.Emoji;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.mj.tne.objects.ICommand;
import de.mj.tne.utils.Data;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CommandManager extends ListenerAdapter {
    private static final Map<String, ICommand> commands = new HashMap<>();

    public CommandManager() {
        addCommand(new JoinCommand());
        addCommand(new PlayCommand());
        addCommand(new PauseCommand());
        addCommand(new ClearPlayListCommand());
        addCommand(new QuitCommand());
        addCommand(new VolumeCommand());
        addCommand(new NextCommand());
        addCommand(new PreviousTrack());
        addCommand(new StatusCommand());
        addCommand(new ShowPlayListCommand());
        addCommand(new StopCommand());
        addCommand(new RadioCommand());
        addCommand(new MemeCommand());
        addCommand(new StorageCommand());

        addCommand(new HelpCommand());
    }

    private void addCommand(ICommand command) {
        if (!commands.containsKey(command.getInvoke())) {
            commands.put(command.getInvoke(), command);
        }
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final String[] split = event.getMessage().getContentRaw().replaceFirst(
                "(?i)" + Pattern.quote(Data.COMMAND_PREFIX), "").split("\\s+");
        final String invoke = split[0].toLowerCase();

        if (commands.containsKey(invoke)) {
            Role role = event.getGuild().getRoleById("574010805326970880");
            if (event.getMember().getRoles().contains(role)) {
                final List<String> args = Arrays.asList(split).subList(1, split.length);

                commands.get(invoke).handle(args, event);
            } else
                event.getChannel().sendMessage(Utils.message(Color.RED, null, "I'm sorry but you don't have the permission to control the bot!")).queue();
        } else {
            if (!event.getAuthor().isBot() && event.getChannel().getId().equalsIgnoreCase("412303243331371010")) {
                event.getChannel().sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Unknown command! Try !help to see all possible de.mj.tne.commands!")).queue();
            }
        }
    }

    public static Map<String, ICommand> getCommands() {
        return commands;
    }
}
