package com.pk.publisher.testutils;

import com.pk.publisher.AbstractConnectionHandler;
import com.pk.publisher.Publisher;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.sd.ConsumerManager;

/**
 * Created by PavelK on 9/10/2016.
 */
public class TestConnectionHandler extends AbstractConnectionHandler{

    public TestConnectionHandler(ConsumerManager consumerManager, IEventCollector eventCollector, int maxRetry) {
        super(consumerManager, eventCollector, maxRetry);
    }

    @Override
    public Publisher getPublisher() {
        // this method need to be overridden to dynamically find right publisher
        return null;
    }
}
