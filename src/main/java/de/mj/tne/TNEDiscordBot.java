package de.mj.tne;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import de.mj.tne.listener.MessageListener;
import de.mj.tne.listener.PlayerManager;
import de.mj.tne.listener.ReadyListener;
import de.mj.tne.manager.CommandManager;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import de.mj.tne.utils.Data;
import de.mj.tne.utils.MineCraftData;
import de.mj.tne.utils.Utils;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TNEDiscordBot {

    public static JDA jda;
    public static VoiceChannel voiceChannel = null;
    public static AudioManager audioManager = null;
    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
    private static ScheduledFuture<?> readScheduler, pirateCraftScheduler;

    private static final String APPLICATION_NAME = "TNE-DiscordBot";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static Sheets service;

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        try {
            startBot();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startScheduler();
    }

    private static void startBot() throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(Data.TOKEN)
                .setAutoReconnect(true)
                .setActivity(Activity.playing(Data.GAME))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(new MessageListener())
                .addEventListeners(new ReadyListener())
                .addEventListeners(new CommandManager())
                .addEventListeners(new PlayerManager());
        jda = builder.build();
    }

    private static void startScheduler() {
        readScheduler = executorService.scheduleAtFixedRate(() -> {
            InputStream inputStream = System.in;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                if (!bufferedReader.ready())
                    return;
                String line = bufferedReader.readLine();
                if (line.equalsIgnoreCase("update")) {
                    update();
                    return;
                }
                if (line.equalsIgnoreCase("stop")) {
                    pirateCraftScheduler.cancel(true);
                    readScheduler.cancel(true);
                    jda.getGuilds().get(0).getTextChannelById(Data.CHANNEL_INFORMATION).sendMessage(Utils.message(Color.RED, "Bot status update", ":sos: [TNE] Bot is now offline!")).queue();
                    System.exit(0);
                } else
                    jda.getTextChannelById(Data.CHANNEL_INFORMATION).sendMessage(
                            Utils.message(Color.BLACK, "Message from console", line)).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, 0, 100, TimeUnit.MILLISECONDS);
        pirateCraftScheduler = executorService.scheduleAtFixedRate(TNEDiscordBot::update, 0, 1, TimeUnit.HOURS);
    }

    private static void update() {
        TextChannel textChannel = jda.getTextChannelById("638509507319955466");
        if (textChannel == null) {
            return;
        }
        textChannel.sendTyping();
        long messageID = 0;
        if (textChannel.hasLatestMessage()) {
            messageID = textChannel.getLatestMessageIdLong();
        }
        MineCraftData minecraftData = new MineCraftData("mc.piratemc.com", 25565);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
        if (minecraftData.isServerUp()) {
            if (messageID == 0) {
                textChannel.sendMessage(Utils.message(Color.GREEN, "PirateMC Server Status",
                        ":white_check_mark: Server is online\n"
                                + "with " + minecraftData.getCurrentPlayers() + " of " + minecraftData.getMaximumPlayers() + " players!"
                                + "\nTime: " + dateTimeFormatter.format(LocalDateTime.now())
                                + "\n Latency: " + minecraftData.getLatency() + " ms")).queue();
            } else {
                textChannel.editMessageById(messageID, Utils.message(Color.GREEN, "PirateMC Server Status", ":white_check_mark: Server is online\n"
                        + "with " + minecraftData.getCurrentPlayers() + " of " + minecraftData.getMaximumPlayers() + " players!"
                        + "\nTime: " + dateTimeFormatter.format(LocalDateTime.now())
                        + "\n Latency: " + minecraftData.getLatency() + " ms")).queue();
            }
        } else {
            if (messageID == 0)
                textChannel.sendMessage(Utils.message(Color.RED, "PirateMC Server Status", ":sos: Server is offline..."
                        + "Time: " + dateTimeFormatter.format(LocalDateTime.now()))).queue();
            else
                textChannel.editMessageById(messageID, Utils.message(Color.RED, "PirateMC Server Status", ":sos: Server is offline...")).queue();
        }
        if (!(audioManager == null) && !(voiceChannel == null))
            if (!(voiceChannel.getMembers().size() > 1)) {
                TextChannel botChannel = jda.getTextChannelById("412303243331371010");
                audioManager.closeAudioConnection();
                audioManager = null;
                voiceChannel = null;
                botChannel.sendMessage(Utils.message(Color.ORANGE, ":x:", "Disabled connection to audiochannel because nobody is inside. :frowning: I am alone, help me!")).queue();
            }
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
