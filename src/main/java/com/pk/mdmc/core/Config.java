package com.pk.mdmc.core;

/**
 * Created by PavelK on 4/8/2016.
 */
public class Config {
    private int windowMaxWidth;
    private int packetMaxSize;
    private int msgMaxPackets;

    private int disruptorRingSize;

    private boolean netTraceEnabled;


    public int getWindowMaxWidth() { return windowMaxWidth; }
    public int getPacketMaxSize() { return packetMaxSize; }
    public int getMsgMaxPackets() { return msgMaxPackets; }

    public int getDisruptorRingSize() { return disruptorRingSize;}

    public boolean getNetTraceEnabled() { return netTraceEnabled; }


    public Config() {
        windowMaxWidth = 5;
        packetMaxSize = 1024;
        msgMaxPackets = 127;

        disruptorRingSize = 16;

        netTraceEnabled = true;
    }
}
