package com.pk.mdmc.impl;

/**
 * Created by PavelK on 3/6/2016.
 */

import com.pk.mdmc.core.IConfig;
import com.pk.mdmc.core.IMessageHandler;
import com.pk.mdmc.core.Packet;
import com.pk.mdmc.core.PacketAssembler;

import java.io.IOException;

import java.net.*;

public class MDMCClient {
    protected final IConfig config;
    protected final IMessageHandler handler;
    protected final MulticastSocket socket;
    protected final PacketAssembler assembler;
    protected Packet packet;

    private MDMCClient() {
        config = null;
        handler = null;
        socket = null;
        assembler = null;
    }

    public MDMCClient(IConfig config, IMessageHandler handler) throws IOException {
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


}
