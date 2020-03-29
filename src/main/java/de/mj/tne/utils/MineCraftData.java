package de.mj.tne.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MineCraftData {
    private static final byte NUM_FIELDS = 6;
    private static final int DEFAULT_TIMEOUT = 100;

    private String address;

    private int port;

    private int timeout;

    private boolean serverUp;

    private String motd;

    private String version;

    private String currentPlayers;

    private String maximumPlayers;

    private long latency;

    public MineCraftData(String address, int port) {
        this(address, port, DEFAULT_TIMEOUT);
    }

    public MineCraftData(String address, int port, int timeout) {
        setAddress(address);
        setPort(port);
        setTimeout(timeout);
        refresh();
    }

    /**
     * Refresh state of the server
     *
     */
    private void refresh() {
        String[] serverData;
        String rawServerData;
        try {
            //Socket clientSocket = new Socket(getAddress(), getPort());
            Socket clientSocket = new Socket();
            long startTime = System.currentTimeMillis();
            clientSocket.connect(new InetSocketAddress(getAddress(), getPort()), timeout);
            setLatency(System.currentTimeMillis() - startTime);
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            byte[] payload = {(byte) 0xFE, (byte) 0x01};
            //dos.writeBytes("\u00FE\u0001");
            dos.write(payload, 0, payload.length);
            rawServerData = br.readLine();
            clientSocket.close();
        } catch (Exception e) {
            serverUp = false;
            //e.printStackTrace();
            return;
        }

        if (rawServerData == null)
            serverUp = false;
        else {
            serverData = rawServerData.split("\u0000\u0000\u0000");
            if (serverData.length >= NUM_FIELDS) {
                serverUp = true;
                setVersion(serverData[2].replace("\u0000", ""));
                setMotd(serverData[3].replace("\u0000", ""));
                setCurrentPlayers(serverData[4].replace("\u0000", ""));
                setMaximumPlayers(serverData[5].replace("\u0000", ""));
            } else
                serverUp = false;
        }
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private int getPort() {
        return port;
    }

    private void setPort(int port) {
        this.port = port;
    }

    private void setTimeout(int timeout) {
        this.timeout = timeout * 1000;
    }

    public String getMotd() {
        return motd;
    }

    public String getVersion() {
        return version;
    }

    public String getCurrentPlayers() {
        return currentPlayers;
    }

    public String getMaximumPlayers() {
        return maximumPlayers;
    }

    public long getLatency() {
        return latency;
    }

    private void setLatency(long latency) {
        this.latency = latency;
    }

    private void setMaximumPlayers(String maximumPlayers) {
        this.maximumPlayers = maximumPlayers;
    }

    private void setCurrentPlayers(String currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    private void setMotd(String motd) {
        this.motd = motd;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    public boolean isServerUp() {
        return serverUp;
    }
}
