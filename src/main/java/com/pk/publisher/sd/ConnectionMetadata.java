package com.pk.publisher.sd;

import com.pk.publisher.core.ClientConnection;

/**
 * Created by PavelK on 6/17/2016.
 */
public class ConnectionMetadata extends ConnectionLookup{
    private final String ipString;
    private final byte[] ipBytes;
    private final int heartbeat;
    private final Consumer consumer;

    private ConnectionMetadata(){
        ip = 0;
        ipString = null;
        ipBytes = null;
        heartbeat = 0;
        consumer = null;
    }

    /**
     * * @param heartbeat in # of ticks, if 10 and arb tick is every 50 ms heartbeat will be sent every 500 msec
     */
    ConnectionMetadata(Consumer owner, String ip, int heartbeat) throws Exception {
        this.consumer = owner;
        this.ipString = ip;
        this.ipBytes = ip.getBytes();
        this.ip = ip2long(ip);
        this.heartbeat = heartbeat;
        consumer.getInstitution().getManager().register(this);
    }

    public final String getIpString() {
        return ipString;
    }

    public final byte[] getIpBytes() {
        return ipBytes;
    }

    public final Consumer getConsumer() {
        return consumer;
    }

    public final int getHeartbeatInterval(){
        return heartbeat;
    }

    public final boolean registerSession(ClientConnection session){
        return consumer.incConnCount();
    }

    public final void unregisterSession(ClientConnection session){
        consumer.decConnCount();
    }

}
