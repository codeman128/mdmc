package com.pk.mdmc.test;

import com.pk.mdmc.core.IConfig;
import com.pk.mdmc.core.IMessageHandler;
import com.pk.mdmc.core.Message;
import com.pk.mdmc.impl.MDMCClient;

import java.io.IOException;

/**
 * Created by pkapovski on 4/21/2016.
 */
public class ClientTest {


    public static void main(String[] args) throws IOException {
        IConfig config = new MDMCConfig();
        IMessageHandler handler = new IMessageHandler() {
            long counter =0;
            long sTime = System.currentTimeMillis();

            @Override
            public void onEvent(Message message, long l, boolean b) throws Exception {
                counter++;
                if (counter==30000) {
                    long t = System.currentTimeMillis();
                    System.out.println(counter+ " messages received "+(t-sTime));
                    counter =  0;
                    sTime = t;
                }
            }
        };

        MDMCClient client = new MDMCClient(config, handler);

        while (true) {
            client.readPacket();
        }

    }
}
