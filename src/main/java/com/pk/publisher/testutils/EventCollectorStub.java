package com.pk.publisher.testutils;

import com.pk.publisher.ClientConnection;
import com.pk.publisher.IEventCollector;

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
    public void onConnectionRejected_Invalid() {
        System.out.println("Connection Rejected - Invalid");
    }

    @Override
    public void onConnectionRejected_Busy() {
        System.out.println("Connection Rejected - Busy");

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
    public void onConnectionAssignError(ClientConnection clientConnection, IOException e) {
        System.out.println("Connection Assign Error");
        e.printStackTrace();
    }

    @Override
    public void onConnectionWriteError(ClientConnection clientConnection, Exception e) {
        System.out.println("Connection Write Error, connection closed and released");
        e.printStackTrace();
    }
}
