package com.pk.publisher;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.sd.ConnectionLookup;
import com.pk.publisher.sd.ConnectionMetadata;
import com.pk.publisher.sd.ConsumerManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Listener implements Runnable{
    private final INewConnectionHandler handler;
    private final byte[] name;
    private final byte[] longName;
    private final int port;
    private final InetAddress address;
    private final boolean tcpNoDelay;
    private final int maxRetry;
    private final int sendBufferSize;

    private final Publisher publisher;
    private final IEventCollector eventCollector;
    private final Thread thread;
    private final ConsumerManager consumerManager;
    private final ConnectionLookup  lookup = new ConnectionLookup();
    private ServerSocket serverSocket;

    private Listener(){
        handler = null;
        port = 0;
        address = null;
        name = null;
        longName = null;
        tcpNoDelay = true;
        maxRetry = 0;
        sendBufferSize = 0;

        publisher = null;
        eventCollector = null;
        thread = null;
        consumerManager = null;
    }

    public Listener(INewConnectionHandler handler, String name, InetAddress address, int port,
                    boolean tcpNoDelay, int maxRetry, int sendBufferSize, Publisher publisher) {
        this.handler = handler;
        this.name = name.getBytes();
        this.port = port;
        this.address = address;
        this.tcpNoDelay = tcpNoDelay;
        this.maxRetry = maxRetry;
        this.sendBufferSize = sendBufferSize;
        String longNameString = (name+" "+address+":"+port);
        this.longName = longNameString.getBytes();

        this.publisher = publisher;
        eventCollector = publisher.getEventCollector();
        consumerManager = publisher.getConsumerManager();

        try {
            serverSocket = new ServerSocket(port, 1, address); //todo configure backlog
        } catch (IOException e) {
            eventCollector.onListenerStartFailed(longName, e);
            System.exit(-1);
        }

        thread = new Thread(this);
        thread.setName("Listener Thread: " +longNameString);
        thread.start();
    }

    protected ConnectionMetadata validateNewConnection(Socket socket) {
        lookup.setIpBytes(socket.getInetAddress().getAddress());
        ConnectionMetadata mData = consumerManager.getConnection(lookup);

        if (mData==null) {
            eventCollector.onConnectionRejected_UnknownConsumer(longName, socket.getInetAddress().toString());
            return null;
        }

        if (mData.getConsumer().getConnCount()==mData.getConsumer().getSimConnLimit()) {
            eventCollector.onConnectionRejected_LimitReached(longName, mData);
            return null;
        }
        // System.out.println("Listener-validate "+mData.getConsumer().getConnCount());
        return mData;
    }

    @Override
    public void run() {



        int sndbuf_old = 0;
        int sndbuf_new = 0;
        try {
            while (true) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(10);
                    clientSocket.setTcpNoDelay(tcpNoDelay);
                    sndbuf_old = clientSocket.getSendBufferSize();
                    if (sendBufferSize>0) clientSocket.setSendBufferSize(sendBufferSize);
                    sndbuf_new = clientSocket.getSendBufferSize();

                } catch (Exception e) {
                    eventCollector.onUnexpectedAcceptorError(longName, e);
                    break;
                }

                // validate new connection request --------------------------------------------------------------
                ConnectionMetadata mData = validateNewConnection(clientSocket);
                if (mData == null) {
                    closeQuietly(clientSocket);
                } else {

                    // let's try to find free slot
                    int retry = 1;
                    ClientConnection connection;
                    while (retry < maxRetry) {
                        retry++;
                        connection = publisher.getAvailableConnection();
                        if (connection != null && connection.assign(clientSocket, mData)) {
                            eventCollector.onConnectionAccepted(longName, connection);
                            break;
                        }
                    }

                    // handle no available spot found
                    if (retry >= maxRetry) {
                        eventCollector.onConnectionRejected_Busy(longName, mData);
                        closeQuietly(clientSocket);
                    }
                }
                // validate new connection request --------------------------------------------------------------
            }
        } finally {
            ///System.out.println("\n\n\n???????????????\n\n\n");
        }
    }

    protected ServerSocket getServerSocket(){
        return serverSocket;
    }

    private void closeQuietly(Socket socket){
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
