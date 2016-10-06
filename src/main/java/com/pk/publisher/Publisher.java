package com.pk.publisher;

import com.pk.publisher.core.*;
import com.pk.publisher.sd.ConsumerManager;

import java.net.UnknownHostException;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Publisher  {
    private final byte[] name;
    private final IPublisherConfig config;
    private final IEventCollector eventCollector;
    private final Feeder[] feeders;
    private final MessageDisruptor disruptor;

    private Publisher(){
        name = null;
        config = null;
        eventCollector = null;
        feeders = null;
        disruptor = null;
    }

    public Publisher(byte[] name, IPublisherConfig config, IEventCollector eventCollector, Monitor monitor){
        this.name = name;
        this.config = config;
        this.eventCollector = eventCollector;


        // init feeders
        feeders = new Feeder[config.getFeederCount()];
        for (byte i=0; i<feeders.length; i++) {
            feeders[i] = new Feeder(i, this);
            monitor.register(feeders[i]);
        }

        // init disruptor
        disruptor = new MessageDisruptor(this);

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



    public IEventCollector getEventCollector(){
        return eventCollector;
    }


    public void shutdown() {
        for (byte i=0; i<feeders.length; i++) {
            feeders[i].shutdown();
        }
        disruptor.shutdown();
    }
}
