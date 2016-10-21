package com.pk.publisher.testutils;

import com.pk.publisher.ConnectionHandler;
import com.pk.publisher.DistributionLayer;
import com.pk.publisher.Publisher;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.IListenerConfig;
import com.pk.publisher.core.Message;
import com.pk.publisher.sd.Consumer;
import com.pk.publisher.sd.ConsumerManager;
import com.pk.publisher.sd.Institution;
import com.pk.publisher.testutils.ClientTest;
import com.pk.publisher.testutils.EventCollectorStub;
import com.pk.publisher.testutils.PublisherConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Created by PavelK on 5/21/2016.
 */
public class ServerTest {

    public static void main(String[] args) throws Exception {
        int idx = 0;
        String configPath = null;
        if (args.length>0) {

            if (args[idx].startsWith("path=")){
                configPath = args[idx].substring(5);
                System.out.println("found config param: "+configPath);
                idx++;
            }

            if ((idx<args.length) && args[idx].equalsIgnoreCase("client")) {
                ClientTest.runTest(configPath);
                System.exit(0);
            }
        }

        // load config
        PublisherConfig config = new PublisherConfig(configPath);

        // initialize consumer manager
        ConsumerManager cm = new ConsumerManager();

        // initialize event collector
        IEventCollector ec = new EventCollectorStub();

        // initialize new connection handler
        ConnectionHandler handler = new ConnectionHandler(cm, ec, config.getAcceptorMaxRetry());

        // initiate listener config
        IListenerConfig lConfig = new IListenerConfig() {
            @Override
            public InetAddress getAddress() {
                try {
                    return config.getAddress();
                } catch (UnknownHostException e) {
                    // todo this need to be checked during configuration validation.
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public int getPort() {
                return config.getPort();
            }

            @Override
            public ConnectionHandler getConnectionHandler() {
                return handler;
            }

            @Override
            public boolean getTcpNoDelay() {
                return true;
            }

            @Override
            public int getSendBufferSize() {
                return 0;
            }

            @Override
            public IEventCollector getEventCollector() {
                return ec;
            }

            @Override
            public int getSnapshotWriteTimeout() {
                return config.getMonitorSnapshotWriteTimeout();
            }

            @Override
            public int getUpdateWriteTimeout() {
                return config.getMonitorWriteTimeout();
            }

            @Override
            public int getBacklog() {
                return 1;
            }
        };

        // initialize distribution layer
        DistributionLayer dl = new DistributionLayer(ec, handler);

        // add publisher
        Publisher publisher_L2 = dl.addPublisher(config);

        Institution i1 = cm.addInstitution("EBS");
        Consumer c1 = i1.addConsumer("L2", 20, 5, publisher_L2); // 20 - number of simultaneously supported connection
        //  5 - is heartbeat in # of ticks, if arb tick is every 50 ms heartbeat will be sent every 250 msec
        c1.addConnection("192.168.1.115");
        c1.addConnection("10.72.2.185");

        // add listener
        dl.addListener(lConfig);


        long time;
        Message msg;
        System.gc();

        int snapshotTickCounter=0;
        while (true){
            time = System.currentTimeMillis();

            msg = publisher_L2.getNext();
            System.arraycopy(config.update, 0, msg.getBuffer(), msg.offset+82, config.update.length);
            msg.length = config.update.length;
            msg.type = Message.TYPE.UPDATE;
            msg.eventTime = System.currentTimeMillis();
            msg.captureTime = System.currentTimeMillis();
            msg.captureNano = System.nanoTime();
            publisher_L2.publish(msg);

            if (snapshotTickCounter==config.snapshotTick) {
                snapshotTickCounter = 0;
                msg = publisher_L2.getNext();
                System.arraycopy(config.snapshot, 0, msg.getBuffer(), msg.offset+82, config.snapshot.length);
                msg.length = config.snapshot.length;
                msg.type = Message.TYPE.SNAPSHOT;
                msg.eventTime = System.currentTimeMillis();
                msg.captureTime = System.currentTimeMillis();
                msg.captureNano = System.nanoTime();
                publisher_L2.publish(msg);
            } else {
                snapshotTickCounter++;
            }

            try {
                Thread.sleep(config.tick);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }
}
