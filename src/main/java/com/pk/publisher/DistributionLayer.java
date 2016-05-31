package com.pk.publisher;

import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.IPublisherConfig;

/**
 * Created by PavelK on 5/31/2016.
 */
public class DistributionLayer {
    private final Monitor monitor;

    private DistributionLayer(){
        monitor = null;
    }

    public DistributionLayer(IEventCollector eventCollector){
        monitor = new Monitor(null, eventCollector);
    }

    public Monitor getMonitor(){
        return monitor;
    }

    public void shutdown(){
        monitor.shutdown();

    }

}
