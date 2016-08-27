package com.pk.redis;

import com.pk.lang.ImmutableInteger;
import com.pk.lang.ImmutableString;
import com.pk.lang.MutableString;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by PavelK on 8/26/2016.
 */
public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private final RedisStringBuilder rsb = new RedisStringBuilder(1024);
    private final byte[] inBuf = new byte[1024];
    private final byte[] oneByte = new byte[1];

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public boolean SET(ImmutableString key, ImmutableString value) throws IOException {
        rsb.appendCommand(Commands.SET, 2).appendString(key).appendString(value);
        rsb.write(out);
        out.flush();
        return readOK();
    }

    public boolean GET(ImmutableString key, MutableString value) throws IOException {
        rsb.appendCommand(Commands.GET, 1).appendString(key);
        rsb.write(out);
        out.flush();
        return readString(value);
    }

    private boolean readString(MutableString value) throws IOException {
        int len = readLine(0);
        if (inBuf[0]=='$') {
            int size = ImmutableInteger.toInt(inBuf, 1, len-3);
            len = readLine(0);
            if ((len-2)==size) {
                value.copyFrom(inBuf, 0, size);
            } else {
                System.out.println("Error reading string invalid length");
            }

        } else handleError(len);
        return false;
    }


    private int readLine(int offset) throws IOException {
        while (true) {
            in.read(oneByte, 0, 1);
            inBuf[offset++]=oneByte[0];
            if (oneByte[0]=='\n' && offset>0 && inBuf[offset-2]=='\r'){
                System.out.println("read ["+new String(inBuf, 0, offset)+"]");
                return offset;
            }
        }
    }

    private void handleError(int lenth){
        if (inBuf[0]=='-') {
            System.out.println("Error: "+new String(inBuf, 0, lenth));
        } else {
            System.out.println("Unknown Error: "+new String(inBuf, 0, lenth));
        }
    }

    private boolean readOK() throws IOException {
        int offset = 0;
        offset = readLine(offset);

        if (offset==5 && inBuf[1]=='O' && inBuf[2]=='K' && inBuf[3]=='\r' && inBuf[4]=='\n')  {
            return true;
        } else {
            handleError(offset);
            return false;
        }
    }
}
