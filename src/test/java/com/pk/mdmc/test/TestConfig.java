package com.pk.mdmc.test;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.pk.mdmc.AbstractConfig;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by pkapovski on 4/20/2016.
 */
public class TestConfig extends AbstractConfig{




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

        disruptorWaitStrategy = new LiteBlockingWaitStrategy();
        //new BusySpinWaitStrategy();
    }

    public TestConfig() {
        mcPort = 8888;
        mcHost = "224.0.0.3";
        mcInterfaceName = "eth6";//3-6
        mcTTL = 1;


        windowMaxWidth = 5;
        packetMaxSize = 1024+24;
        msgMaxPackets = 127;

        disruptorRingSize = 32;

        netTraceEnabled = false;

        init();
    }
}
