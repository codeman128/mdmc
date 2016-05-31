package com.pk.publisher;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.core.Feeder;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.IPublisherConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 5/29/2016.
 */
public class Monitor implements Runnable{
    private final static String THREAD_NAME = "MONITOR";
    private final IPublisherConfig config;
    private final IEventCollector eventCollector;
    private final Thread thread;
    private final long writeTimeout;
    private final List<Feeder> feeders;

    private volatile int syncPoint;
    private volatile boolean exitFlag;

    private Monitor(){
        config = null;
        eventCollector = null;
        thread = null;
        writeTimeout = 0;
        feeders = null;
    }

    public Monitor(IPublisherConfig config, IEventCollector eventCollector) {
        exitFlag = false;
        this.config = config;
        this.eventCollector = eventCollector;
        writeTimeout = config.getMonitorWriteTimeout();
        thread = new Thread(this);
        thread.setName(THREAD_NAME);
        thread.start();
        feeders = new ArrayList<Feeder>();
    }

    public final void register(Feeder feeder){
        feeders.add(feeder);
    }

    @Override
    public void run() {
        Feeder feeder;
        ClientConnection con;
        try {
            while (!exitFlag) {
                for (int i = 0; i < feeders.size(); i++) {
                    feeder = feeders.get(i);
                    syncPoint++;
                    con = feeder.getMonConnection();
                    if (con != null) {
                        long delta = System.nanoTime() - feeder.getMonTime();
                        if (delta > writeTimeout) {
                            eventCollector.onMonitorWriteTimeout(con, delta);
                            con.safelyCloseConnection();
                        }
                    }
                }
                try {
                    Thread.sleep(0, config.getMonitorSleep());
                } catch (InterruptedException e) {
                    // quietly?
                }
            }
        } finally {
            eventCollector.onMonitorShutdown();
        }
    }

    public void shutdown(){
        exitFlag = true;
        thread.interrupt();
    }
}
