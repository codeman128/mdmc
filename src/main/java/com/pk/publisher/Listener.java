package com.pk.publisher;

import com.pk.publisher.core.IListenerConfig;
import com.pk.publisher.sd.ConnectionMetadata;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Listener implements Runnable{
    private final IListenerConfig config;
    private final byte[] name;
    private final Thread thread;
    private ServerSocket serverSocket;
    private volatile boolean shutdown = false;

    private Listener(){
        config = null;
        name = null;
        thread = null;
    }

    public Listener(IListenerConfig config) {
        this.config = config;
        String nameString = (config.getAddress()+":"+config.getPort());
        this.name = nameString.getBytes();


        try {
            serverSocket = new ServerSocket(config.getPort(), config.getBacklog(), config.getAddress());
        } catch (IOException e) {
            config.getEventCollector().onListenerStartFailed(name, e);
            System.exit(-1);  // todo replace - initiate proper shutdown!
        }

        thread = new Thread(this);
        thread.setName("MDS Listener: " +nameString);
        thread.start();
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
                    clientSocket.setTcpNoDelay(config.getTcpNoDelay());
                    sndbuf_old = clientSocket.getSendBufferSize();
                    if (config.getSendBufferSize()>0) clientSocket.setSendBufferSize(config.getSendBufferSize());
                    sndbuf_new = clientSocket.getSendBufferSize();

                } catch (Exception e) {
                    if (!shutdown) config.getEventCollector().onUnexpectedListenerError(name, e);
                    break;
                }

                ConnectionMetadata mData = config.getConnectionHandler().handleConnection(this, clientSocket);
            }
        } finally {
            config.getEventCollector().onListenerShutdown(name);
        }
    }

    public byte[] getName(){
        return name;
    }

    public IListenerConfig getConfig(){
        return config;
    }

    public void shutdown(){
        shutdown = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }

}
