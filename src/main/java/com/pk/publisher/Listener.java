package com.pk.publisher;

import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.sd.ConnectionMetadata;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Listener implements Runnable{
    private final byte[] name;
    private final int port;
    private final InetAddress address;
    private final boolean tcpNoDelay;
    private final int sendBufferSize;

    private final AbstractConnectionHandler handler;
    private final IEventCollector eventCollector;
    private final Thread thread;
    private ServerSocket serverSocket;

    private Listener(){
        name = null;
        port = 0;
        address = null;
        tcpNoDelay = true;
        sendBufferSize = 0;
        handler = null;
        eventCollector = null;
        thread = null;
    }

    public Listener(AbstractConnectionHandler handler, InetAddress address, int port,
                    boolean tcpNoDelay, int sendBufferSize, IEventCollector ec) {
        String nameString = (address+":"+port);
        this.name = nameString.getBytes();
        this.port = port;
        this.address = address;
        this.tcpNoDelay = tcpNoDelay;
        this.sendBufferSize = sendBufferSize;

        this.eventCollector = ec;
        this.handler = handler;

        try {
            serverSocket = new ServerSocket(port, 1, address); //todo configure backlog param
        } catch (IOException e) {
            eventCollector.onListenerStartFailed(name, e);
            System.exit(-1);
        }

        thread = new Thread(this);
        thread.setName("Listener Thread: " +nameString);
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
                    clientSocket.setTcpNoDelay(tcpNoDelay);
                    sndbuf_old = clientSocket.getSendBufferSize();
                    if (sendBufferSize>0) clientSocket.setSendBufferSize(sendBufferSize);
                    sndbuf_new = clientSocket.getSendBufferSize();

                } catch (Exception e) {
                    eventCollector.onUnexpectedListenerError(name, e);
                    break;
                }

                // validate new connection request --------------------------------------------------------------
                ConnectionMetadata mData = handler.handleConnection(this, clientSocket);
                // validate new connection request --------------------------------------------------------------
            }
        } finally {
            ///System.out.println("\n\n\n???????????????\n\n\n");
        }
    }

    public byte[] getName(){
        return name;
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
