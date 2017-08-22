package com.pk.mdmc.test;

import com.pk.mdmc.client.PacketAssembler;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * Created by PavelK on 8/21/2017.
 */
public class MMFTest {

    static int length = 10*1024*1024;


    public static void server(String interfaceName, String group, int mcPort, int payloadSize, int msgCount) throws IOException {
        System.out.println("Starting Server at ["+interfaceName+"] ["+group+"] ["+mcPort+"] payload:["+payloadSize+"] msg count:["+msgCount+"]");
        InetAddress mcGroup =  InetAddress.getByName(group);
        int mcTTL = 1;
        MulticastSocket socket = new MulticastSocket();
        socket.setNetworkInterface(NetworkInterface.getByName(interfaceName));
        socket.setTimeToLive(mcTTL);
        socket.setLoopbackMode(true); // set disabled (true)


        // todo for socket.setTimeToLive(x);
        // http://www.peterfranza.com/2008/09/25/setting-multicast-time-to-live-in-java/
        // -Djava.net.preferIPv4Stack=true

        DatagramPacket datagram = new DatagramPacket(new byte[payloadSize], payloadSize);
        datagram.setAddress(mcGroup);
        datagram.setPort(mcPort);
        for (int i=0; i<msgCount; i++) {
            socket.send(datagram);
        }
    }


    public static void clinet(String interfaceName, String group, int mcPort) throws IOException {
        System.out.println("Starting Client at ["+interfaceName+"] ["+group+"] ["+mcPort+"]");
        InetAddress mcGroup =  InetAddress.getByName(group);
        MulticastSocket socket = new MulticastSocket(mcPort);
        socket.setNetworkInterface(NetworkInterface.getByName(interfaceName));
        socket.joinGroup(mcGroup);

        DatagramPacket datagram = new DatagramPacket(new byte[10*1024], 10*1024);

        int i = 0;
        int j=0;
        while (true) {
            socket.receive(datagram);
            i++;
            j++;
            if (i==1000) {
                System.out.println(j);
                i=0;
            }
        }
        //socket.leaveGroup(mcGroup);
    }


    public static void main(String[] args) throws IOException {
        long start;
        long stop;

        if (args.length==1 && args[0].equalsIgnoreCase("CLIENT")) {
            clinet("eth6", "224.0.0.3", 8888);
        }

        start= System.nanoTime();
        server("eth6", "224.0.0.3", 8888, 1024, 50*1000+1);
        stop = System.nanoTime();
        System.out.println((stop-start)/1000000);
        System.exit(-1);




        int[] address = new int[length];
        Random random = new Random(System.currentTimeMillis());

        for (int i=0; i<length; i++){

            address[i] = (int) (Math.random() * (length - 8));

        }


        MappedByteBuffer out = new RandomAccessFile("C:\\temp\\howtodoinjava.dat", "rw")
                .getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);

        //System.out.println(out.getClass().getName());
        for (int i = 0; i < length; i++)
        {
            out.put((byte) 'x');
        }
        System.out.println("Finished writing");


    for (int k=0; k<10; k++) {
        start = System.nanoTime();
        for (int l=0; l<100; l++) {
            for (int i = 0; i < length; i++) {
                int j = address[i];
                out.putInt(j, j);
            }
        }
        stop = System.nanoTime();

        System.out.println((stop - start)/1000000);
    }


    }
}
