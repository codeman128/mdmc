package com.pk.publisher;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Publisher {
    protected ServerSocket server;
    protected final IPublisherConfig config;
    public final Feeder[] feeders; //make protected/private
    protected final Acceptor acceptor;

    private Publisher(){
        config = null;
        feeders = null;
        acceptor = null;
    }

    public Publisher(IPublisherConfig config){
        this.config = config;

        // init feeders
        feeders = new Feeder[config.getFeederCount()];
        for (int i=0; i<feeders.length; i++) {
            feeders[i] = new Feeder(this);
        }

        try {
            server = new ServerSocket(config.getPort(), 1); //todo proper bind
        } catch (IOException e) {
            e.printStackTrace(); //todo proper error log handling
        }

        // init acceptor
        acceptor = new Acceptor(this);


    }

    public IPublisherConfig getConfig(){
        return config;
    }

    public ClientConnection getAvailableConnection() {
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
}
