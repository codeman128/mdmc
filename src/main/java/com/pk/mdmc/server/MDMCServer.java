package com.pk.mdmc.server;

/**
 * Created by PavelK on 3/6/2016.
 */


import com.pk.mdmc.IConfig;
import com.pk.mdmc.Packet;

import java.io.IOException;
import java.net.MulticastSocket;

public class MDMCServer {
    protected final IConfig config;
    protected final MulticastSocket socket;
    protected final PacketDisruptor disruptor;
    protected final IPacketHandler handler = new IPacketHandler() {
        @Override
        public void onEvent(Packet packet, long l, boolean b) throws Exception {
            socket.send(packet.getDatagram());
        }
    };

    private MDMCServer() {
        config = null;
        socket = null;
        disruptor = null;
    }

    public MDMCServer(IConfig config) throws IOException {
        this.config = config;
        socket = new MulticastSocket();
        socket.setNetworkInterface(config.getNetInterface());
        socket.setTimeToLive(config.getTTL());
        socket.setLoopbackMode(true); // set disabled (true)
        disruptor = new PacketDisruptor(config, handler);
    }

    public IPacketBuffer getDisruptor() {
        return disruptor;
    }

}

// todo for socket.setTimeToLive(x);
// http://www.peterfranza.com/2008/09/25/setting-multicast-time-to-live-in-java/
// -Djava.net.preferIPv4Stack=true