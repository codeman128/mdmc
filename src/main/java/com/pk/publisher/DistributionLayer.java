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
    private final List<Publisher> publishers;
    private final IEventCollector eventCollector;
    private final ConsumerManager consumerManager;

    private DistributionLayer(){
        monitor = null;
        publishers = null;
        eventCollector = null;
        consumerManager = null;
    }

    public DistributionLayer(IEventCollector eventCollector){
        this.eventCollector = eventCollector;
        consumerManager = new ConsumerManager(this);
        monitor = new Monitor(100000, eventCollector);
        publishers = new ArrayList<>();
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
