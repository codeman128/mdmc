package com.pk.publisher;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Acceptor implements Runnable{
    private final static String THREAD_NAME_SUFFIX = " - ACCEPTOR";
    private final Publisher publisher;
    private final IPublisherConfig config;
    private final IEventCollector eventEmitter;
    private final Thread thread;

    private Acceptor(){
        publisher = null;
        config = null;
        eventEmitter = null;
        thread = null;
    }

    public Acceptor(Publisher publisher) {
        this.publisher = publisher;
        config = publisher.getConfig();
        eventEmitter = publisher.getEventEmitter();
        thread = new Thread(this);
        thread.setName(new String(publisher.getName())+THREAD_NAME_SUFFIX);
        thread.start();
    }

    protected boolean validateNewConnection(Socket socket) {
        if (1==2) eventEmitter.onConnectionRejected_Invalid();//todo implement
        return true;
    }

    @Override
    public void run() {
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = publisher.getServerSocket().accept();
                clientSocket.setSoTimeout(10);
            } catch (Exception e) {
                eventEmitter.onUnexpectedAcceptorError(e);
                System.exit(-1);
                break;
            }

            // validate new connection request
            if (!validateNewConnection(clientSocket)) {
                closeQuietly(clientSocket);
            }

            // let's try to find free slot
            int retry = 1;
            ClientConnection connection;
            while (retry<config.getAcceptorMaxRetry()){
                retry++;
                connection = publisher.getAvailableConnection();
                if (connection!=null && connection.assign(clientSocket)) {
                    eventEmitter.onConnectionAccepted(connection);
                    break;
                }
            }

            // handle no available spot found
            if (retry>=config.getAcceptorMaxRetry()){
                eventEmitter.onConnectionRejected_Busy();
                closeQuietly(clientSocket);
            }
        }
    }

    private void closeQuietly(Socket socket){
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
