package com.pk.mdmc.client;

import com.lmax.disruptor.EventHandler;
import com.pk.mdmc.Message;

/**
 * Created by pkapovski on 4/20/2016.
 */
public interface IMessageHandler extends EventHandler<Message> {
}
