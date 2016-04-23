package com.pk.mdmc.test;

import com.pk.mdmc.IConfig;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by pkapovski on 4/20/2016.
 */
public class MDMCConfig implements IConfig{

    private int mcPort;
    private String mcHost;
    private InetAddress mcAddress;
    protected int mcTTL;

    protected int windowMaxWidth;
    protected int packetMaxSize;
    protected int msgMaxPackets;

    protected int disruptorRingSize;

    protected boolean netTraceEnabled;
    protected NetworkInterface netInterface;
    private String mcInterfaceName;

    @Override
    public int getWindowMaxWidth() { return windowMaxWidth; }

    @Override
    public int getPacketMaxSize() { return packetMaxSize; }

    @Override
    public int getMsgMaxPackets() { return msgMaxPackets; }

    @Override
    public int getDisruptorRingSize() { return disruptorRingSize;}

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
    public int getTTL() { return mcTTL; }

    @Override
    public boolean getNetTraceEnabled() {
        return netTraceEnabled;
    }


    protected void init() {
        try {
            mcAddress = InetAddress.getByName(mcHost);
        } catch (UnknownHostException e) {
            e.printStackTrace(); //todo add log
        }

        try {
            netInterface = NetworkInterface.getByName(mcInterfaceName);
        } catch (SocketException e) {
            e.printStackTrace(); //todo add log
        }
    }

    public MDMCConfig() {
        mcPort = 8888;
        mcHost = "224.0.0.3";
        mcInterfaceName = "eth3";//eth6
        mcTTL = 2;


        windowMaxWidth = 5;
        packetMaxSize = 1024;
        msgMaxPackets = 127;

        disruptorRingSize = 32;

        netTraceEnabled = false;

        init();
    }
}
