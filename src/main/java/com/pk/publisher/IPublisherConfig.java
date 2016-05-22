package com.pk.publisher;

import com.lmax.disruptor.WaitStrategy;

/**
 * Created by PavelK on 5/21/2016.
 */
public interface IPublisherConfig {
    int getPort();
    int getFeederCount();
    int getMaxClientConnection();
    int getAcceptorMaxRetry();

    int getDisruptorRingSize();

    int getMaxMessageSize();

    WaitStrategy getDisruptorStrategy();
}
