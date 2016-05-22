package com.pk.publisher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Acceptor implements Runnable{
    private final Publisher publisher;
    protected final IPublisherConfig config;
    protected final Thread thread;

    private Acceptor(){
        publisher = null;
        config = null;
        thread = null;
    }

    public Acceptor(Publisher publisher) {
        this.publisher = publisher;
        config = publisher.config;
        thread = new Thread(this);
        thread.setName("ACCEPTOR"); //todo add id;
        thread.start();
    }

    protected boolean validateNewConnection(Socket socket) {
        return true;
    }

    @Override
    public void run() {
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = publisher.server.accept();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            // validate new connection request
            if (!validateNewConnection(clientSocket)) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Connection refused!"); //todo proper handling
            }

            // let's try to find free slot
            int retry = 1;
            ClientConnection connection;
            while (retry<config.getAcceptorMaxRetry()){
                connection = publisher.getAvailableConnection();
                if (connection!=null && connection.assign(clientSocket)) break;
            }
            if (retry>=config.getAcceptorMaxRetry()){
                //todo log no slots available
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
