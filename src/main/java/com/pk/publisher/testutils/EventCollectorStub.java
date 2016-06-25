package com.pk.publisher.testutils;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.Message;
import com.pk.publisher.sd.ConnectionMetadata;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by pkapovski on 5/26/2016.
 */
public class EventCollectorStub implements IEventCollector {
    @Override
    public void onConnectionAccepted(ClientConnection connection) {
        Socket s = connection.getSocket();
        System.out.println("New connection accepted from ["+ s.getInetAddress()+":"+s.getPort()+"] assigned to C["
                +connection.getId()+"] F["+connection.getFeeder().getId()+"]");
    }

    @Override
    public void onConnectionRejected_UnknownConsumer(String address) {
        System.out.println("Connection Rejected - Unknown from address: "+address);
    }

    @Override
    public void onConnectionRejected_Busy(ConnectionMetadata mData) {
        System.out.println("Connection from "+mData+" Rejected - Busy");

    }

    @Override
    public void onConnectionRejected_LimitReached(ConnectionMetadata mData) {
        System.out.println("Connection from "+mData +" rejected, simulations connection limit reached "+mData.getConsumer().getSimConnLimit());
    }

    @Override
    public void onUnexpectedAcceptorError(Exception e) {
        System.out.println("onUnexpectedAcceptorError\n");
        e.printStackTrace();
    }

    @Override
    public void onBindFailed(int port, IOException e) {
        System.out.println("Bind to port "+port+" failed.");
        e.printStackTrace();
    }

    @Override
    public void onConnectionAssignError(ClientConnection clientConnection, ConnectionMetadata mData, IOException e) {
        System.out.println("Connection Assign Error");
        e.printStackTrace();
    }

    @Override
    public void onConnectionWriteError(ClientConnection clientConnection, ConnectionMetadata mData, Exception e) {
        System.out.println("Connection Write Error, connection closed and released");
    }

    @Override
    public void onMonitorWriteTimeout(ClientConnection clientConnection, long timeNano, long timeout, Message.TYPE msgType) {
        System.out.println("Monitor - write timeout detected, stale for "+timeNano+" expected "+timeout+" when publishing "+msgType.name());
    }

    @Override
    public void onMonitorShutdown() {
        System.out.println("Monitor shutdown");
    }

    @Override
    public void onMonitorException(Exception e) {
        System.out.println("Monitor - Unknown Exception - "+e.toString());
        e.printStackTrace();
    }
}
