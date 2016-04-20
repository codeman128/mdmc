package com.pk.mdmc.impl;

import com.pk.mdmc.core.IConfig;

/**
 * Created by pkapovski on 4/20/2016.
 */
public class MDMCConfig implements IConfig{
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


    public MDMCConfig() {
        windowMaxWidth = 5;
        packetMaxSize = 1024;
        msgMaxPackets = 127;

        disruptorRingSize = 16;

        netTraceEnabled = true;
    }
}
