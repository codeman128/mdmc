package com.pk.publisher.core;

import com.lmax.disruptor.WaitStrategy;

/**
 * Created by PavelK on 5/21/2016.
 */
public interface IPublisherConfig {
    /** Product Name */
    byte[] getName();

    /** Number of dedicated threads */
    int getFeederCount();

    /** Number of connection slots per feeder (thread) */
    int getMaxClientConnection();

    int getMaxMessageSize();

    int getDisruptorRingSize();

    WaitStrategy getDisruptorStrategy();

    /** Return - new offset **/
    int addMsgSeqId(byte[] buffer, int offset, long id);

    boolean shouldAddHeader();

    /** Graceful shutdown interval in milliseconds */
    int getGracefulShutdownInterval();

    /** Latency floor 0 - default/disabled, positive value - mds latency floor in micros, negative supported but not documented arb tick latency floor subject to clock synchronization */
    long getLatencyFloor();
}
