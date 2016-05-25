package com.pk.mdmc.test;

import com.pk.mdmc.IConfig;
import com.pk.mdmc.Packet;
import com.pk.mdmc.server.IPacketRingBuffer;
import com.pk.mdmc.server.Server;

import java.io.IOException;

/**
 * Created by PavelK on 4/21/2016.
 */
public class ServerTest {
    static long messagesToSend = 1000*1000*1000;
    static int segmentCount = 2;


    public static void main(String[] args) throws IOException, InterruptedException {
        IConfig config = new TestConfig();

        Server server = new Server(config);
        Packet packet;
        IPacketRingBuffer disruptor = server.getDisruptor();

        for (long i = 0; i < messagesToSend; i++) {

            for ( int j = 1; j<=segmentCount; j++) {
                packet = disruptor.next();
                packet.setSequenceId(i);
                packet.setSegmentCount((short) segmentCount);
                packet.setPayloadSize((short) 1000);
                packet.setSegmentId((short) 1);
                disruptor.publish();
            }

            //System.out.println("Server sent: "+packet);
            //Thread.sleep(1);
        }

    }
}
