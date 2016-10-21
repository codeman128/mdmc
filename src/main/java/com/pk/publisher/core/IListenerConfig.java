package com.pk.publisher.core;

import com.pk.publisher.ConnectionHandler;

import java.net.InetAddress;

/**
 * Created by PavelK on 10/21/2016.
 */
public interface IListenerConfig {
    InetAddress getAddress();
    int getPort();
    ConnectionHandler getConnectionHandler();
    /** Nagle's Algorithm **/
    boolean getTcpNoDelay();
    int getSendBufferSize();
    IEventCollector getEventCollector();
    int getSnapshotWriteTimeout();
    int getUpdateWriteTimeout();

    /** requested maximum length of the queue of incoming connections **/
    int getBacklog();
}
