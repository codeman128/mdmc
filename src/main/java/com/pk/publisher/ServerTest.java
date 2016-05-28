package com.pk.publisher;

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

        IPublisherConfig config = new PublisherConfig();
        Publisher publisher = new Publisher("L1".getBytes(), config, new EventCollectorStub());

        long time;
        Message msg;

        while (true){
            time = System.currentTimeMillis();
            String update_str = "UPDATE "+time+"\n";
            String snapshot_str = "SNAPSHOT "+time+"\n";

            msg = publisher.getNext();
            System.arraycopy(update_str.getBytes(), 0, msg.getBuffer(), 0, update_str.length());
            msg.length = update_str.length();
            msg.type = Message.TYPE.UPDATE;
            msg.eventTime = System.nanoTime();
            publisher.publish(msg);

            msg = publisher.getNext();
            System.arraycopy(snapshot_str.getBytes(), 0, msg.getBuffer(), 0, snapshot_str.length());
            msg.length = snapshot_str.length();
            msg.type = Message.TYPE.SNAPSHOT;
            msg.eventTime = System.nanoTime();
            publisher.publish(msg);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }
}
