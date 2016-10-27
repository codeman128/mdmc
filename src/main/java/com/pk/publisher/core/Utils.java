package com.pk.publisher.core;

import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Created by pkapovski on 10/27/2016.
 */
public class Utils {

    /** Allocates memory - for debug only */
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    /**  Example [0] [0] [192] [168] [1] [1]  [port 1 byte] [port 0 byte] */
    public static long encodeAddress(InetAddress address, int port) {
        long data = 0;
        for (int i=0; i<address.getAddress().length; i++){
            data = (data <<8) | (0xFF & address.getAddress()[i]);
        }
        data = data<<16;
        data = data | port;
        return data;
    }

    /** Allocates memory - for debug only */  //todo not tested for 6 bytes IPs!
    public static String decodeAddress(long address) {
        StringBuilder sb = new StringBuilder(25);

        byte[] buf = longToBytes(address);

        int start = (buf[0]==0 && buf[1]==0)? 2 : 0;

        for (int i=start; i<6; i++){
            sb.append(0xff & buf[i]);
            if (i<5) sb.append("."); else sb.append(":");
        }
        sb.append(0xFFFF & address);
        return sb.toString();
    }
}
