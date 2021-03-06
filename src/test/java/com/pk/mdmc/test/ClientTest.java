package com.pk.mdmc.test;

import com.pk.mdmc.IConfig;
import com.pk.mdmc.client.IMessageHandler;
import com.pk.mdmc.Message;
import com.pk.mdmc.client.Client;

import java.io.IOException;

/**
 * Created by pkapovski on 4/21/2016.
 */
public class ClientTest {


    public static void main(String[] args) throws IOException {
        IConfig config = new TestConfig();
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

        Client client = new Client(config, handler);

        while (true) {
            client.readPacket();
        }

    }
}
