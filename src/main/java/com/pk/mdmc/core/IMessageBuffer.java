package com.pk.mdmc.core;

/**
 * Created by PavelK on 4/13/2016.
 */
public interface IMessageBuffer {
    Message next();
    void push(Message msg);
}
