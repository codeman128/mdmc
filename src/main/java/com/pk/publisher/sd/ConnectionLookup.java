package com.pk.publisher.sd;

/**
 * Created by PavelK on 6/17/2016.
 */
public class ConnectionLookup {
    protected long ip;

    @Override
    public final int hashCode() {
        return (int)ip;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof ConnectionLookup) {
            return ip == ((ConnectionLookup)obj).ip;
        }
        return false;
    }

    public final long getIp(){
        return ip;
    }

    public void setIpBytes(byte[] ipBytes){
        ip = ipBytes2long(ipBytes);
    }

    // parse IP parts into long
    static long ip2long(String ip){
        long l = 0;
        String[] parts = ip.split("\\.");
        for (String part : parts) {
            l = (l << 8) | (byte)Integer.parseInt(part);
        }
        return l;
    }

    // parse IP bytes into long
    static public long ipBytes2long(byte[] ip){
        long l = 0;
        for (byte b : ip) {
            l = (l << 8) | b;
        }
        return l;
    }

}
