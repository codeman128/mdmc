package com.pk.mdmc.client;

/**
 * Created by PavelK on 3/6/2016.
 */

import com.pk.mdmc.IConfig;
import com.pk.mdmc.Packet;
import java.io.IOException;

import java.net.*;

public class Client {
    protected final IConfig config;
    protected final IMessageHandler handler;
    protected final MulticastSocket socket;
    protected final PacketAssembler assembler;
    protected Packet packet;

    private Client() {
        config = null;
        handler = null;
        socket = null;
        assembler = null;
    }

    public Client(IConfig config, IMessageHandler handler) throws IOException {
        this.config = config;
        this.handler = handler;
        packet = new Packet(config);
        socket = new MulticastSocket(config.getPort());
        socket.setNetworkInterface(config.getNetInterface());
        socket.joinGroup(config.getAddress());
        assembler = new PacketAssembler(config, handler);
        assembler.init();
    }

    public void readPacket() throws IOException {
        socket.receive(packet.getDatagram());
        packet = assembler.push(packet);
    }

    public void close() throws IOException {
        socket.leaveGroup(config.getAddress());
    }


}
