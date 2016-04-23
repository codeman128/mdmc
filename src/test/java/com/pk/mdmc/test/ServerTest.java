package com.pk.mdmc.test;

import com.pk.mdmc.core.IConfig;
import com.pk.mdmc.core.Packet;
import com.pk.mdmc.impl.MDMCServer;

import java.io.IOException;

/**
 * Created by PavelK on 4/21/2016.
 */
public class ServerTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        IConfig config = new MDMCConfig();

        MDMCServer server = new MDMCServer(config);
        Packet packet = server.getPacket();

        for (long i = 0; i < 50000000; i++) {

            packet.setSequenceId(i);
            packet.setSegmentCount((short)2);
            packet.setPayloadSize((short)150);
            packet.setSegmentId((short)1);

            server.send();
            //System.out.println("Server sent: "+packet);

            packet.setSegmentId((short)2);
            server.send();

            //System.out.println("Server sent: "+packet);
            //Thread.sleep(1);
        }

    }
}
