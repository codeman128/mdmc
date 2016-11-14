package com.pk.publisher.sd;

import com.pk.publisher.Publisher;

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
    private final int heartbeat;
            final AtomicInteger simConnCounter = new AtomicInteger(0);
    private Publisher publisher;
    private final byte[] longName;

    private Consumer(){
        owner = null;
        name = null;
        nameBytes = null;
        simConnLimit = 0;
        heartbeat = 0;
        longName = null;
    }

    /**
     * @param simConnLimit simultaneous connections limit
     * @param heartbeat in # of ticks, if 10 and arb tick is every 50 ms heartbeat will be sent every 500 msec
     * */
    Consumer(Institution owner, String name, int simConnLimit, int heartbeat, Publisher publisher){
        this.owner = owner;
        this.name = name;
        this.nameBytes = name.getBytes();
        this.simConnLimit = simConnLimit;
        this.heartbeat = heartbeat;
        this.publisher = publisher;
        this.longName = (name+"@"+owner.getName()).getBytes();
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


     public ConnectionMetadata addConnection(String ip) throws Exception {
        final ConnectionMetadata con = new ConnectionMetadata(this, ip);
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

    public int getHeartbeat(){
        return heartbeat;
    }

    public Publisher getPublisher(){ return publisher;}

    public byte[] getLongNameBytes() {
        return longName;
    }
}
