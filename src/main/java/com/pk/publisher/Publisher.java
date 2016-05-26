package com.pk.publisher;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Publisher  {
    private final byte[] name;
    private final IPublisherConfig config;
    private final IEventCollector eventEmitter;
    private final Feeder[] feeders;
    private final Acceptor acceptor;
    private final MessageDisruptor disruptor;
    private ServerSocket serverSocket;

    private Publisher(){
        name = null;
        config = null;
        eventEmitter = null;
        feeders = null;
        acceptor = null;
        disruptor = null;
    }

    public Publisher(byte[] name, IPublisherConfig config, IEventCollector eventEmitter){
        this.name = name;
        this.config = config;
        this.eventEmitter = eventEmitter;

        // init feeders
        feeders = new Feeder[config.getFeederCount()];
        for (byte i=0; i<feeders.length; i++) {
            feeders[i] = new Feeder(i, this);
        }

        // init disruptor
        disruptor = new MessageDisruptor(this);

        try {
            serverSocket = new ServerSocket(config.getPort(), 1); //todo proper bind
        } catch (IOException e) {
            eventEmitter.onBindFailed(config.getPort(), e);
            System.exit(-1);
        }

        // init acceptor
        acceptor = new Acceptor(this);
    }

    public byte[] getName(){
        return name;
    }

    public IPublisherConfig getConfig(){
        return config;
    }

    protected ClientConnection getAvailableConnection() {
        int maxAvailable = 0;
        Feeder lazy = null;
        for (int i = 0; i < feeders.length; i++) {
            if (feeders[i].countAvailable()>maxAvailable){
                lazy = feeders[i];
                maxAvailable = lazy.countAvailable();
            }
        }
        if (maxAvailable>0){
            return lazy.getFirstAvailable();
        }
        return null;
    }

    public Message getNext(){
        return disruptor.next();
    }

    public void publish(Message message) {
        disruptor.push(message);
    }

    public Feeder[] getFeeders(){
        return feeders;
    }

    protected ServerSocket getServerSocket(){
        return serverSocket;
    }

    public IEventCollector getEventEmitter(){
        return eventEmitter;
    }
}
