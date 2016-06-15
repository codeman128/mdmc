package com.pk.publisher.testutils;

import com.pk.publisher.DistributionLayer;
import com.pk.publisher.Publisher;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.Message;
import com.pk.publisher.testutils.ClientTest;
import com.pk.publisher.testutils.EventCollectorStub;
import com.pk.publisher.testutils.PublisherConfig;



/**
 * Created by PavelK on 5/21/2016.
 */
public class ServerTest {

     public static void main(String[] args) throws Exception {

        if (args.length>0 && args[0].equalsIgnoreCase("client")){
            ClientTest.runTest();
            System.exit(0);
        }


         IEventCollector ec = new EventCollectorStub();
         DistributionLayer dl = new DistributionLayer(ec);

         PublisherConfig config = new PublisherConfig();
         Publisher publisher_L2 = dl.addPublisher("L1".getBytes(), config);


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
            msg.eventTime = System.nanoTime();
            publisher_L2.publish(msg);

            if (snapshotTickCounter==config.snapshotTick) {
                snapshotTickCounter = 0;
                msg = publisher_L2.getNext();
                System.arraycopy(config.snapshot, 0, msg.getBuffer(), msg.offset+82, config.snapshot.length);
                msg.length = config.snapshot.length;
                msg.type = Message.TYPE.SNAPSHOT;
                msg.eventTime = System.nanoTime();
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
