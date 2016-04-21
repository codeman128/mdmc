package com.pk.mdmc.impl;

/**
 * Created by PavelK on 3/6/2016.
 */


import com.pk.mdmc.core.IConfig;
import com.pk.mdmc.core.Packet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;

public class MDMCServer {
    protected final IConfig config;
    protected final Packet packet;
    protected final MulticastSocket serverSocket;

    private MDMCServer() {
        config = null;
        packet = null;
        serverSocket = null;
    }

    public MDMCServer(IConfig config) throws IOException {
        this.config = config;
        packet = new Packet(config);
        serverSocket = new MulticastSocket();
        serverSocket.setNetworkInterface(config.getNetInterface());
    }

    public Packet getPacket() {
        return packet;
    }

    public void send() throws IOException {
        serverSocket.send(packet.getDatagram());
    }

}