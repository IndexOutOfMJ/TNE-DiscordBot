package de.mj.tne.games;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public class CounterGame {

    public static void countUpdate(TextChannel textChannel, @NotNull Message message) {
        int number = -1;
        try {
            number = Integer.parseInt(message.getContentDisplay());
        } catch (NumberFormatException e) {
            message.delete().queue();
        }
        String title = textChannel.getTopic();
        if (title != null && title.startsWith("Next number: ")) {
            String nextS = title.replaceFirst("Next number: ", "");
            int next = -1;

            try {
                next = Integer.parseInt(nextS);
            } catch (NumberFormatException e) {
                setupChannel(textChannel);
                return;
            }
            if (number != next) {
                message.delete().queue();
                return;
            }
            next++;
            textChannel.getManager().setTopic("Next number: " + (next)).queue();
        } else {
            setupChannel(textChannel);
        }
    }

    private static void setupChannel(@NotNull TextChannel textChannel) {
        textChannel.purgeMessages(MessageHistory.getHistoryFromBeginning(textChannel).complete().getRetrievedHistory());
        textChannel.getManager().setTopic("Next number: 1").queue();
    }
}
