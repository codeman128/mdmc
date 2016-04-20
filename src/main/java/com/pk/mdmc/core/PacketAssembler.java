package com.pk.mdmc.core;

/**
 * Created by pkapovski on 4/20/2016.
 */
public class PacketAssembler {
    private final IConfig config;
    private final IMessageBuffer messageDisruptor;
    private Window window;

    private PacketAssembler(){
        config = null;
        messageDisruptor = null;
        window = null;
    }

    public PacketAssembler(IConfig config, IMessageHandler handler){
        this.config = config;
        this.messageDisruptor = new MessageDisruptor(config, handler);
        this.window = new Window(config, messageDisruptor);
    }

    public void init() {
        window.init();
    }

    public Packet push(Packet packet){
        return window.processPacket(packet);
    }

}
