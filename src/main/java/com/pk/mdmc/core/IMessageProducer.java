package com.pk.mdmc.core;

/**
 * Created by PavelK on 4/13/2016.
 */
public interface IMessageProducer {

    public Message next();
    public void push(Message msg);

}
