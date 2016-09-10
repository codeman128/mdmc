package com.pk.publisher.testutils;

import com.pk.publisher.DistributionLayer;
import com.pk.publisher.Publisher;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.Message;
import com.pk.publisher.sd.Consumer;
import com.pk.publisher.sd.ConsumerManager;
import com.pk.publisher.sd.Institution;
import com.pk.publisher.testutils.ClientTest;
import com.pk.publisher.testutils.EventCollectorStub;
import com.pk.publisher.testutils.PublisherConfig;



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
        Institution i1 = cm.addInstitution("EBS");
        Consumer c1 = i1.addConsumer("L1", 20); // 20 - number of simultaneously supported connection
        c1.addConnection("192.168.1.115",5); // 5 - is heartbeat in # of ticks, if arb tick is every 50 ms heartbeat will be sent every 250 msec
        c1.addConnection("10.72.2.185",5); // 5 - is heartbeat in # of ticks, if arb tick is every 50 ms heartbeat will be sent every 250 msec

        // initialize event collector
        IEventCollector ec = new EventCollectorStub();

        // initialize new connection handler
        TestConnectionHandler handler = new TestConnectionHandler(cm, ec, config.getAcceptorMaxRetry());

        // initialize distribution layer
        DistributionLayer dl = new DistributionLayer(ec, handler);

        // add publisher
        Publisher publisher_L2 = dl.addPublisher("L1".getBytes(), config);
        //this iis a hack, proper connection handler shell determine publisher based on ip..
        handler.publisher = publisher_L2;

        // add listener
        dl.addListener(config.getAddress(), config.getPort(), config.getTcpNoDelay(), config.getSendBufferSize());



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
