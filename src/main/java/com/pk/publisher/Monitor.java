package com.pk.publisher;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.core.Feeder;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.IPublisherConfig;

import java.io.IOException;

/**
 * Created by PavelK on 5/29/2016.
 */
public class Monitor implements Runnable{
    private final static String THREAD_NAME = "MONITOR";
    private final Publisher publisher;
    private final IPublisherConfig config;
    private final IEventCollector eventCollector;
    private final Thread thread;
    private final long writeTimeout;

    private volatile int syncPoint;

    private Monitor(){
        publisher = null;
        config = null;
        eventCollector = null;
        thread = null;
        writeTimeout = 0;
    }

    public Monitor(Publisher publisher) {
        this.publisher = publisher;
        config = publisher.getConfig();
        eventCollector = publisher.getEventCollector();
        writeTimeout = config.getMonitorWriteTimeout();
        thread = new Thread(this);
        thread.setName(THREAD_NAME);
        thread.start();
    }

    @Override
    public void run() {
        Feeder feeder;
        while (true) {
            syncPoint++;
            for (int i = 0; i < publisher.getFeeders().length; i++) {
                feeder = publisher.getFeeders()[i];
                ClientConnection monConnection = feeder.getMonConnection();
                if (monConnection!=null ) {
                    long delta = System.nanoTime()-feeder.getMonTime();
                    if (delta > writeTimeout) {
                        eventCollector.onMonitorWriteTimeout(monConnection, delta);
                        try {
                            monConnection.getSocket().close();
                        } catch (IOException e) {
                            e.printStackTrace();//todo
                        }
                    }
                }

            }
            try {
                Thread.sleep(config.getMonitorSleep());
            } catch (InterruptedException e) {
                e.printStackTrace();  //todo
            }
        }

    }
}
