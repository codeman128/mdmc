package com.pk.mdmc.test;

import com.pk.mdmc.IConfig;
import com.pk.mdmc.Packet;
import com.pk.mdmc.server.IPacketRingBuffer;
import com.pk.mdmc.server.MDMCServer;

import java.io.IOException;

/**
 * Created by PavelK on 4/21/2016.
 */
public class ServerTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        IConfig config = new MDMCConfig();

        MDMCServer server = new MDMCServer(config);
        Packet packet;
        IPacketRingBuffer disruptor = server.getDisruptor();

        for (long i = 0; i < 50000000; i++) {
            packet = disruptor.next();
            packet.setSequenceId(i);
            packet.setSegmentCount((short)2);
            packet.setPayloadSize((short)180);
            packet.setSegmentId((short)1);
            disruptor.publish();
            //System.out.println("Server sent: "+packet);

            packet = disruptor.next();
            packet.setSequenceId(i);
            packet.setSegmentCount((short)2);
            packet.setPayloadSize((short)180);
            packet.setSegmentId((short)2);
            disruptor.publish();
            //System.out.println("Server sent: "+packet);
            //Thread.sleep(1);
        }

    }
}
