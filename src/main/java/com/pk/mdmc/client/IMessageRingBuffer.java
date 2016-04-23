package com.pk.mdmc.client;

import com.pk.mdmc.Message;

/**
 * Created by PavelK on 4/13/2016.
 */
public interface IMessageRingBuffer {
    Message next();
    void push(Message msg);
}
