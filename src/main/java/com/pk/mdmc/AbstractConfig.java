package com.pk.mdmc;

import com.lmax.disruptor.WaitStrategy;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created by pkapovski on 4/24/2016.
 */
public abstract class AbstractConfig implements IConfig {
    protected int mcPort;
    protected String mcHost;
    protected InetAddress mcAddress;
    protected int mcTTL;

    protected int windowMaxWidth;
    protected int packetMaxSize;
    protected int msgMaxPackets;

    protected int disruptorRingSize;
    protected WaitStrategy disruptorWaitStrategy;

    protected boolean netTraceEnabled;
    protected NetworkInterface netInterface;
    protected String mcInterfaceName;

    @Override
    public int getWindowMaxWidth() {
        return windowMaxWidth;
    }

    @Override
    public int getPacketMaxSize() {
        return packetMaxSize;
    }

    @Override
    public int getMsgMaxPackets() {
        return msgMaxPackets;
    }

    @Override
    public int getDisruptorRingSize() {
        return disruptorRingSize;
    }

    @Override
    public WaitStrategy getDisruptorStrategy() {
        return disruptorWaitStrategy;
    }

    @Override
    public int getPort() {
        return mcPort;
    }

    @Override
    public String getHost() {
        return mcHost;
    }

    @Override
    public InetAddress getAddress() {
        return mcAddress;
    }

    @Override
    public String getNetInterfaceName() {
        return mcInterfaceName;
    }

    @Override
    public NetworkInterface getNetInterface() {
        return netInterface;
    }

    @Override
    public int getTTL() {
        return mcTTL;
    }

    @Override
    public boolean getNetTraceEnabled() {
        return netTraceEnabled;
    }
}
