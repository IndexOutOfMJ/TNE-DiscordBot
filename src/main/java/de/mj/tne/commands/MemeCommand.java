package de.mj.tne.commands;

import de.mj.tne.constants.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import de.mj.tne.objects.ICommand;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import de.mj.tne.utils.Utils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class MemeCommand implements ICommand {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        JSONParser parser = new JSONParser();
        String postLink = "";
        String title = "";
        String url = "";

        if (event.getChannel().getIdLong() != 693831683513909249L) {
            event.getMessage().delete().queue();
            PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessage(Utils.message(Color.ORANGE, Emoji.ERROR, "Please send bot de.mj.tne.commands only into the #bot-control channel!")).queue();
            return;
        }
        try {
            URL memeURL = new URL("https://meme-api.herokuapp.com/gimme");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(memeURL.openConnection().getInputStream()));

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                JSONArray array = new JSONArray();
                array.add(parser.parse(lines));

                for (Object o : array) {
                    JSONObject jsonObject = (JSONObject) o;

                    postLink = (String) jsonObject.get("postLink");
                    title = (String) jsonObject.get("title");
                    url = (String) jsonObject.get("url");
                }
            }
            bufferedReader.close();

            event.getMessage().delete().queue();
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(title, postLink)
                    .setImage(url)
                    .setColor(Color.ORANGE);
            event.getChannel().sendMessage(builder.build()).queue();

        } catch (Exception e) {
            event.getChannel().sendMessage(":no_entry: **Hey, something went wrong. Please try again later!**").queue();
            e.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return "Sends a meme.";
    }

    @Override
    public String getInvoke() {
        return "meme";
    }
}
