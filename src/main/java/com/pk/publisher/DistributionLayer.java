package com.pk.publisher;

import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.IPublisherConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 5/31/2016.
 */
public class DistributionLayer {
    private final Monitor monitor;
    private final List<Publisher> publishers;
    private final IEventCollector eventCollector;

    private DistributionLayer(){
        monitor = null;
        publishers = null;
        eventCollector = null;
    }

    public DistributionLayer(IEventCollector eventCollector){
        this.eventCollector = eventCollector;
        monitor = new Monitor(1000, eventCollector);

        publishers = new ArrayList<>();
    }

    public Publisher addPublisher(byte[] name, IPublisherConfig config) {
        Publisher p = new Publisher(name, config, eventCollector, monitor);
        publishers.add(p);
        return p;
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
