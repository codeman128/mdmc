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
    private volatile boolean isShutdown = false;

    private Publisher(){
        name = null;
        config = null;
        eventCollector = null;
        feeders = null;
        disruptor = null;
    }

    public Publisher(IPublisherConfig config, IEventCollector eventCollector, Monitor monitor){
        this.config = config;
        this.name = config.getName();
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
        return (isShutdown) ? null : disruptor.next();
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

    private boolean hasBacklog() {
        final long lastSequence = disruptor.getLastAllocSequence();
        for (byte i=0; i<feeders.length; i++) {
            if (lastSequence != feeders[i].getLastProcessedSequence()) return true;
        }
        return false;
    }

    public void shutdown() {
        if (isShutdown) return;
        isShutdown = true;
        eventCollector.onPublisherShutdownStarted(name);

        long start = System.currentTimeMillis();
        while (hasBacklog()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (System.currentTimeMillis() - start > config.getGracefulShutdownInterval()) break;
        }

        for (byte i=0; i<feeders.length; i++) {
            feeders[i].shutdown();
        }
        disruptor.shutdown();
    }
}
