package com.pk.publisher;

import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.IListenerConfig;
import com.pk.publisher.core.IPublisherConfig;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 5/31/2016.
 */
public class DistributionLayer {
    private final Monitor monitor;
    private final List<Listener> listeners = new ArrayList<>();
    private final List<Publisher> publishers = new ArrayList<>();
    private final IEventCollector eventCollector;
    private final ConnectionHandler handler;

    private DistributionLayer(){
        monitor = null;
        eventCollector = null;
        handler = null;
    }

    public DistributionLayer(IEventCollector eventCollector, ConnectionHandler handler){
        this.eventCollector = eventCollector;
        monitor = new Monitor(100000, eventCollector);
        this.handler = handler;
    }

    public final Publisher addPublisher(IPublisherConfig config) {
        Publisher p = new Publisher(config, eventCollector, monitor);
        publishers.add(p);
        return p;
    }

    public final void addListener(IListenerConfig config){
        listeners.add(new Listener(config));
    }

    public Monitor getMonitor(){
        return monitor;
    }

    public void shutdownListeners() {
        for (int i=0; i< listeners.size();i++) {
            listeners.get(i).shutdown();
        }
    }

    public void shutdownPublishers() {
        monitor.shutdown();
        for (int i=0; i< publishers.size();i++) {
            publishers.get(i).shutdown();
        }
    }

}
