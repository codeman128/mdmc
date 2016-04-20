package com.pk.mdmc.impl;

import com.pk.mdmc.core.IConfig;

/**
 * Created by pkapovski on 4/20/2016.
 */
public class MDMCConfig implements IConfig{
    protected int windowMaxWidth;
    protected int packetMaxSize;
    protected int msgMaxPackets;

    protected int disruptorRingSize;

    protected boolean netTraceEnabled;


    public int getWindowMaxWidth() { return windowMaxWidth; }
    public int getPacketMaxSize() { return packetMaxSize; }
    public int getMsgMaxPackets() { return msgMaxPackets; }

    public int getDisruptorRingSize() { return disruptorRingSize;}

    public boolean getNetTraceEnabled() { return netTraceEnabled; }


    public MDMCConfig() {
        windowMaxWidth = 5;
        packetMaxSize = 1024;
        msgMaxPackets = 127;

        disruptorRingSize = 32;

        netTraceEnabled = true;
    }
}
