package com.pk.publisher.core;

import com.lmax.disruptor.WaitStrategy;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Created by PavelK on 5/21/2016.
 */
public interface IPublisherConfig {
    int getPort();
    int getFeederCount();
    int getMaxClientConnection();
    int getAcceptorMaxRetry();

    int getMonitorWriteTimeout();
    int getMonitorSleep();

    int getDisruptorRingSize();

    int getMaxMessageSize();

    WaitStrategy getDisruptorStrategy();

    InetAddress getAddress() throws UnknownHostException;
}
