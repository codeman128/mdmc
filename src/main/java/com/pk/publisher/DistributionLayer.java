package com.pk.publisher;

import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.IPublisherConfig;
import com.pk.publisher.sd.ConsumerManager;

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
    private final ConsumerManager consumerManager;
    private final AbstractConnectionHandler handler;

    private DistributionLayer(){
        monitor = null;
        eventCollector = null;
        consumerManager = null;
        handler = null;
    }

    public DistributionLayer(IEventCollector eventCollector, AbstractConnectionHandler handler, ConsumerManager cm){
        this.eventCollector = eventCollector;
        consumerManager = cm;
        monitor = new Monitor(100000, eventCollector);
        this.handler = handler;
    }

    public final Publisher addPublisher(byte[] name, IPublisherConfig config) {
        Publisher p = new Publisher(name, config, eventCollector, monitor, consumerManager);
        publishers.add(p);
        return p;
    }

    public final ConsumerManager getConsumerManager(){
        return consumerManager;
    }

    public Monitor getMonitor(){
        return monitor;
    }

    public void shutdown() {
        for (int i=0; i< publishers.size();i++) {
            publishers.get(i).shutdown();
        }
        monitor.shutdown();

    }

}
