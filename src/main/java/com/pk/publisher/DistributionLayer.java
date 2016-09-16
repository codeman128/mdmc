package com.pk.publisher;

import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.IPublisherConfig;
import com.pk.publisher.sd.ConsumerManager;

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
    private final AbstractConnectionHandler handler;

    private DistributionLayer(){
        monitor = null;
        eventCollector = null;
        handler = null;
    }

    public DistributionLayer(IEventCollector eventCollector, AbstractConnectionHandler handler){
        this.eventCollector = eventCollector;
        monitor = new Monitor(100000, eventCollector);
        this.handler = handler;
    }

    public final Publisher addPublisher(byte[] name, IPublisherConfig config) {
        Publisher p = new Publisher(name, config, eventCollector, monitor);
        publishers.add(p);
        return p;
    }

    public final void addListener(InetAddress address, int port, boolean tcpNoDelay, int sendBufferSize){
        listeners.add(new Listener(handler, address, port, tcpNoDelay, sendBufferSize, eventCollector));
    }


    public Monitor getMonitor(){
        return monitor;
    }

    public void shutdown() {
        monitor.shutdown();

        for (int i=0; i< listeners.size();i++) {
            listeners.get(i).shutdown();
        }

        for (int i=0; i< publishers.size();i++) {
            publishers.get(i).shutdown();
        }


    }

}
