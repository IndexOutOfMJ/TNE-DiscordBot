package de.mj.tne.games;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Game {
    public void startGame();

    public void endGame();

    public void sendInput(String[] in, MessageReceivedEvent event);
}
