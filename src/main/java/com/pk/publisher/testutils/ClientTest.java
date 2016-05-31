package com.pk.publisher.testutils;

import org.omg.CORBA.UNKNOWN;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.SocketChannel;
import java.nio.file.ProviderMismatchException;
import java.util.Properties;

/**
 * Created by PavelK on 5/28/2016.
 */
public class ClientTest {

    public static void runTest() throws Exception {
        Properties properties = Utils.loadConfig("client");

        InetSocketAddress address = new InetSocketAddress(properties.getProperty("test.publisher.host"),
                Integer.parseInt(properties.getProperty("test.publisher.port")));

        int sleep = Integer.parseInt(properties.getProperty("test.sleep.nanos"));
        int count = Integer.parseInt(properties.getProperty("test.connection.count"));
        SocketChannel[] channels = new SocketChannel[count];
        long[] readData = new long[count];

        for (int i=0; i<count; i++) {
            try {
                channels[i] = SocketChannel.open(address);
            } catch (IOException e) {
                System.out.println("Error establishing connection "+i);
                e.printStackTrace();
            }
            Thread.sleep(5);
        }

        ByteBuffer buf = ByteBuffer.allocate(1024*200);
        int outCounter =50;
        while (1==1) {
            for (int i = 0; i < count; i++) {
                buf.position(0);
                SocketChannel channel = channels[i];
                if (channel!=null) {
                    try {
                        readData[i] += channel.read(buf);

                    } catch (IOException e) {
                        System.out.println("Error reading from connection "+i);
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(0, sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            outCounter++;
            if (outCounter>50) {
                outCounter=0;
                for (int i = 0; i < count; i++) {
                    System.out.print(/*"["+i+"] = */"["+readData[i]/1024+"]   ");
                }
                System.out.println();
            }
        }

    }


    public static void main(String[] args) throws Exception {
        runTest();
    }






}
