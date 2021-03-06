package com.pk.publisher.sd;

import com.pk.publisher.Publisher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 6/17/2016.
 */
public class Institution {
    private final String name;
    private final byte[] nameBytes;
    private final ConsumerManager manager;
    private final List<Consumer> consumers = new ArrayList<>();

    private Institution(){
        name = null;
        nameBytes = null;
        manager = null;
    }

    Institution(ConsumerManager manager, String name){
        this.name = name;
        this.nameBytes = name.getBytes();
        this.manager = manager;
    }

    public final String getName(){
        return name;
    }

    public final byte[] getNameBytes(){
        return nameBytes;
    }

    public final List<Consumer> getConsumers(){
        return consumers;
    }

    /**
     * @param name Consumer name (previously known as client, usually reflect product name)
     * @param simConnLimit simultaneous connections limit
     */
    public final Consumer addConsumer(String name, int simConnLimit, int heartbeat, Publisher publisher){
        Consumer c = new Consumer(this, name, simConnLimit, heartbeat, publisher);
        consumers.add(c);
        return c;
    }

    public final void dropConnections() {
        //todo implement
    }

    final ConsumerManager getManager(){
        return manager;
    }
}
