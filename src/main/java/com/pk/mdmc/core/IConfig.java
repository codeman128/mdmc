package com.pk.mdmc.core;

/**
 * Created by PavelK on 4/8/2016.
 */
public interface IConfig {
    int getWindowMaxWidth();
    int getPacketMaxSize();
    int getMsgMaxPackets();
    int getDisruptorRingSize();
    boolean getNetTraceEnabled();
}
