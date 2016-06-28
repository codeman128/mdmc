package com.pk.publisher;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.IPublisherConfig;
import com.pk.publisher.sd.ConnectionLookup;
import com.pk.publisher.sd.ConnectionMetadata;
import com.pk.publisher.sd.ConsumerManager;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Acceptor implements Runnable{
    private final static String THREAD_NAME_SUFFIX = " - ACCEPTOR";
    private final Publisher publisher;
    private final IPublisherConfig config;
    private final IEventCollector eventCollector;
    private final Thread thread;
    private final ConsumerManager consumerManager;
    private final ConnectionLookup  lookup = new ConnectionLookup();

    private Acceptor(){
        publisher = null;
        config = null;
        eventCollector = null;
        thread = null;
        consumerManager = null;
    }

    public Acceptor(Publisher publisher) {
        this.publisher = publisher;
        config = publisher.getConfig();
        eventCollector = publisher.getEventCollector();
        consumerManager = publisher.getConsumerManager();
        thread = new Thread(this);
        thread.setName(new String(publisher.getName())+THREAD_NAME_SUFFIX);
        thread.start();
    }

    protected ConnectionMetadata validateNewConnection(Socket socket) {
        lookup.setIpBytes(socket.getInetAddress().getAddress());
        ConnectionMetadata mData = consumerManager.getConnection(lookup);

        if (mData==null) {
            eventCollector.onConnectionRejected_UnknownConsumer(socket.getInetAddress().toString());
            return null;
        }

        if (mData.getConsumer().getConnCount()==mData.getConsumer().getSimConnLimit()) {
            eventCollector.onConnectionRejected_LimitReached(mData);
            return null;
        }
        System.out.println("Acceptor-validate "+mData.getConsumer().getConnCount());
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
                    clientSocket = publisher.getServerSocket().accept();
                    clientSocket.setSoTimeout(10);
                    clientSocket.setTcpNoDelay(config.getTcpNoDelay());
                    sndbuf_old = clientSocket.getSendBufferSize();
                    if (config.getSendBufferSize()>0) clientSocket.setSendBufferSize(config.getSendBufferSize());
                    sndbuf_new = clientSocket.getSendBufferSize();

                } catch (Exception e) {
                    eventCollector.onUnexpectedAcceptorError(e);
                    break;
                }

                // validate new connection request
                ConnectionMetadata mData = validateNewConnection(clientSocket);
                if (mData == null) {
                    closeQuietly(clientSocket);
                } else {

                    // let's try to find free slot
                    int retry = 1;
                    ClientConnection connection;
                    while (retry < config.getAcceptorMaxRetry()) {
                        retry++;
                        connection = publisher.getAvailableConnection();
                        if (connection != null && connection.assign(clientSocket, mData)) {
                            eventCollector.onConnectionAccepted(connection);
                            break;
                        }
                    }

                    // handle no available spot found
                    if (retry >= config.getAcceptorMaxRetry()) {
                        eventCollector.onConnectionRejected_Busy(mData);
                        closeQuietly(clientSocket);
                    }
                }
            }
        } finally {
            ///System.out.println("\n\n\n???????????????\n\n\n");
        }
    }

    private void closeQuietly(Socket socket){
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
