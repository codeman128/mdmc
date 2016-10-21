package com.pk.publisher.core;

import com.lmax.disruptor.WaitStrategy;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Created by PavelK on 5/21/2016.
 */
public interface IPublisherConfig {


    int getFeederCount();
    int getMaxClientConnection();



    int getMaxMessageSize();
    int getDisruptorRingSize();
    WaitStrategy getDisruptorStrategy();

    /** Return - new offset **/
    int addMsgSeqId(byte[] buffer, int offset, long id);

    boolean shouldAddHeader();

    /** Graceful shutdown interval in milliseconds */
    int getGracefulShutdownInterval();
}
