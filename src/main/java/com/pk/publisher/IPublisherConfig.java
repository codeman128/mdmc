package com.pk.publisher;

/**
 * Created by PavelK on 5/21/2016.
 */
public interface IPublisherConfig {
    int getPort();
    int getFeederCount();
    int getMaxClientConnection();
    int getAcceptorMaxRetry();
}
