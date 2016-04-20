package com.pk.mdmc.core;

import com.lmax.disruptor.EventHandler;

/**
 * Created by pkapovski on 4/20/2016.
 */
public interface IMessageHandler extends EventHandler<Message> {
}
