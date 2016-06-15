package com.pk.publisher.testutils;

import org.omg.CORBA.UNKNOWN;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.SocketChannel;
import java.nio.file.ProviderMismatchException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by PavelK on 5/28/2016.
 */
public class ClientTest {
    static int sleep;
    static int count;
    static long[] readData;
    static List<SocketChannel> channels;
    static ByteBuffer buf = ByteBuffer.allocate(1024*2000);

    public static void runTest() throws Exception {
        Properties properties = Utils.loadConfig("client");

        InetSocketAddress address = new InetSocketAddress(properties.getProperty("test.publisher.host"),
                Integer.parseInt(properties.getProperty("test.publisher.port")));

        sleep = Integer.parseInt(properties.getProperty("test.sleep.nanos"));
        count = Integer.parseInt(properties.getProperty("test.connection.count"));
        channels = new ArrayList<>();
        readData = new long[count];

        for (int i=0; i<count; i++) {
            try {
                SocketChannel sc = SocketChannel.open(address);
                sc.configureBlocking(false);
                channels.add(sc);
            } catch (IOException e) {
                System.out.println("Error establishing connection "+i);
                e.printStackTrace();
            }
            read();
        }


        int outCounter = 1;
        while (1==1) {
            read();
            outCounter++;
            if (outCounter>1000) {
                outCounter=0;
                for (int i = 0; i < count; i++) {
                    System.out.print(/*"["+i+"] = */"["+readData[i]/1024+"]   ");
                }
                System.out.println();
            }
        }

    }

    private static void read() {
        for (int i = 0; i < channels.size(); i++) {
            buf.position(0);
            try {
                readData[i] += channels.get(i).read(buf);
            } catch (IOException e) {
                System.out.println("Error reading from connection "+i);
                channels.remove(i);
                e.printStackTrace();
            }

        }
        try {
            Thread.sleep(0, sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        runTest();
    }






}
