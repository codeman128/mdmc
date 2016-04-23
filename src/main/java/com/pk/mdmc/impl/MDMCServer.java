package com.pk.mdmc.impl;

/**
 * Created by PavelK on 3/6/2016.
 */


import com.pk.mdmc.core.IConfig;
import com.pk.mdmc.core.Packet;

import java.io.IOException;
import java.net.MulticastSocket;

public class MDMCServer {
    protected final IConfig config;
    protected final Packet packet;
    protected final MulticastSocket socket;

    private MDMCServer() {
        config = null;
        packet = null;
        socket = null;
    }

    public MDMCServer(IConfig config) throws IOException {
        this.config = config;
        packet = new Packet(config);
        socket = new MulticastSocket();
        socket.setNetworkInterface(config.getNetInterface());
        socket.setTimeToLive(config.getTTL());
        socket.setLoopbackMode(true); // set disabled (true)
    }

    public Packet getPacket() {
        return packet;
    }

    public void send() throws IOException {
        socket.send(packet.getDatagram());
    }

}

// todo for socket.setTimeToLive(x);
// http://www.peterfranza.com/2008/09/25/setting-multicast-time-to-live-in-java/
// -Djava.net.preferIPv4Stack=true