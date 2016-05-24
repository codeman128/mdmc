package com.pk.publisher;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Publisher  {
    private final byte[] name;
    private final IPublisherConfig config;
    private final AbstractEventEmitter eventEmitter;
    protected final Feeder[] feeders; //make protected/private
    protected final Acceptor acceptor;
    protected final MessageDisruptor disruptor;
    protected ServerSocket server;

    private Publisher(){
        name = null;
        config = null;
        eventEmitter = null;
        feeders = null;
        acceptor = null;
        disruptor = null;
    }

    public Publisher(byte[] name, IPublisherConfig config, AbstractEventEmitter eventEmitter){
        this.name = name;
        this.config = config;
        this.eventEmitter = eventEmitter;

        // init feeders
        feeders = new Feeder[config.getFeederCount()];
        for (int i=0; i<feeders.length; i++) {
            feeders[i] = new Feeder(this);
        }

        // init disruptor
        disruptor = new MessageDisruptor(this);

        try {
            server = new ServerSocket(config.getPort(), 1); //todo proper bind
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

    public AbstractEventEmitter getEventEmitter(){
        return eventEmitter;
    }
}
