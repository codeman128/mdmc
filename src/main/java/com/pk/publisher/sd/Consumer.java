package com.pk.publisher.sd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by PavelK on 6/17/2016.
 */
public class Consumer {
    private final List<ConnectionMetadata> connections = new ArrayList<>();
    private final Institution owner;
    private final String name;
    private final byte[] nameBytes;
    private final int simConnLimit;
            final AtomicInteger simConnCounter = new AtomicInteger(0);

    private Consumer(){
        owner = null;
        name = null;
        nameBytes = null;
        simConnLimit = 0;
    }

    /**
     * @param simConnLimit simultaneous connections limit
     * */
    Consumer(Institution owner, String name, int simConnLimit){
        this.owner = owner;
        this.name = name;
        this.nameBytes = name.getBytes();
        this.simConnLimit = simConnLimit;
    }

    public final List<ConnectionMetadata> getConnections() {
        return connections;
    }

    public final Institution getInstitution(){
        return owner;
    }

    public final String getName() {
        return name;
    }

    public final byte[] getNameBytes() {
        return nameBytes;
    }

    /**
     * @param heartbeat in # of ticks, if 10 and arb tick is every 50 ms heartbeat will be sent every 500 msec
     **/
     public ConnectionMetadata addConnection(String ip, int heartbeat) throws Exception {
        final ConnectionMetadata con = new ConnectionMetadata(this, ip, heartbeat);
        connections.add(con);
        return con;
    }

    public final int getSimConnLimit(){
        return simConnLimit;
    }

    public final int getConnCount(){
        return simConnCounter.get();
    }

    public final boolean incConnCount(){
        int c;
        while (true) {
            c = simConnCounter.get();
            if (c<simConnLimit) {
                if (simConnCounter.compareAndSet(c, c+1)) return true;
            }  else return false;
        }
    }

    public final int decConnCount() {
        return simConnCounter.decrementAndGet();
    }

}
