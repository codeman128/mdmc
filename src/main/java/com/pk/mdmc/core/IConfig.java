package com.pk.mdmc.core;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created by PavelK on 4/8/2016.
 */
public interface IConfig {

    int getWindowMaxWidth();
    int getPacketMaxSize();
    int getMsgMaxPackets();
    int getDisruptorRingSize();

    int getPort();
    String getHost();
    InetAddress getAddress();
    String getNetInterfaceName();
    NetworkInterface getNetInterface();

    boolean getNetTraceEnabled();
}
