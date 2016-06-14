package com.pk.publisher;

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
         Publisher publisher = dl.addPublisher("L1".getBytes(), config);


        long time;
        Message msg;
        System.gc();

        int snapshotTickCounter=0;
        while (true){
            time = System.currentTimeMillis();

            msg = publisher.getNext();
            System.arraycopy(config.update, 0, msg.getBuffer(), 0, config.update.length);
            msg.offset = 0;
            msg.length = config.update.length;
            msg.type = Message.TYPE.UPDATE;
            msg.eventTime = System.nanoTime();
            publisher.publish(msg);

            if (snapshotTickCounter==config.snapshotTick) {
                snapshotTickCounter = 0;
                msg = publisher.getNext();
                System.arraycopy(config.snapshot, 0, msg.getBuffer(), 0, config.snapshot.length);
                msg.offset = 0;
                msg.length = config.snapshot.length;
                msg.type = Message.TYPE.SNAPSHOT;
                msg.eventTime = System.nanoTime();
                publisher.publish(msg);
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
