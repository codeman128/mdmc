package com.pk.publisher.sd;

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

    public final byte[] getNameBytese(){
        return nameBytes;
    }

    public final List<Consumer> getConsumers(){
        return consumers;
    }

    /**
     * @param name Consumer name (previously known as client, usually reflect product name)
     * @param simConnLimit simultaneous connections limit
     */
    public final Consumer addConsumer(String name, int simConnLimit){
        Consumer c = new Consumer(this, name, simConnLimit);
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
