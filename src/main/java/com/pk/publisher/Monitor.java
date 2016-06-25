package com.pk.publisher;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.core.Feeder;
import com.pk.publisher.core.IEventCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 5/29/2016.
 */
public class Monitor implements Runnable{
    private final static String THREAD_NAME = "MONITOR";
    private final IEventCollector eventCollector;
    private final Thread thread;
    private final List<Feeder> feeders;
    private final int sleep;

    private volatile int syncPoint;
    private volatile boolean exitFlag;

    private Monitor(){
        eventCollector = null;
        thread = null;
        feeders = null;
        sleep = 0;
    }

    public Monitor(int sleep,IEventCollector eventCollector) {
        exitFlag = false;
        this.sleep = sleep;
        this.eventCollector = eventCollector;
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
        ClientConnection session, prevSession= null;
        long now;
        long monTime, prevMonTime = 0;
        long delta;


        try {
            while (!exitFlag) {
                for (int i = 0; i < feeders.size(); i++) {
                    try {
                        feeder = feeders.get(i);
                        syncPoint++;
                        session = feeder.getMonConnection();
                        if (session != null) {
                            now = System.nanoTime();
                            monTime = feeder.getMonTime();
                            delta = now - monTime;

                            // there are overflow and other possible sync issues that need to be handled
                            if (delta < 0 || delta > 1000000000L) { //<0 or >1 sec
                                feeder.setMonTime(now);
                            } else
                            if (delta > feeder.getMonitorWriteTimeout() && monTime != 0) {

                                if (session==prevSession && monTime==prevMonTime) {
                                    // This is to handle funny multithreading issue
                                    // Monitor calling session.safelyCloseConnection() below and continue (this will
                                    // call socket.close(), the socket writing thread is blocker on write() which exit
                                    // with IOException and will call session.safelyCloseConnection() from wring thread
                                    // again etc. Monitor, however, will not wait for it and may "test" same session again
                                    // a specially if there is only one active session, will find same session and will
                                    // try to close it again... we will simply ignore it until it will be cleared off.
                                } else {
                                    eventCollector.onMonitorWriteTimeout(session, delta, feeder.getMonitorWriteTimeout(), feeder.getMonMessageType());
                                    prevSession = session;
                                    prevMonTime = monTime;
                                    session.safelyCloseConnection();
                                }
                            }  else {
                                prevSession = null;
                            }
                        }
                        try {
                            Thread.sleep(0, sleep);
                        } catch (InterruptedException e) {
                            // quietly?
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                         eventCollector.onMonitorException(e);
                    }
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
