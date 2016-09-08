package com.pk.publisher;

import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.sd.ConsumerManager;

/**
 * Created by pkapovski on 9/8/2016.
 */
public abstract class AbstractConnectionHandler implements INewConnectionHandler {
    private final IEventCollector eventCollector;
    private final ConsumerManager consumerManager;

    private AbstractConnectionHandler(){
        consumerManager = null;
        eventCollector = null;
    }

    public AbstractConnectionHandler(ConsumerManager consumerManager, IEventCollector eventCollector){
        this.consumerManager = consumerManager;
        this.eventCollector = eventCollector;
    }

}
