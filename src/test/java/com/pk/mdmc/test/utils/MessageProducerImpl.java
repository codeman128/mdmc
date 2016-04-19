package com.pk.mdmc.test.utils;


import com.pk.mdmc.core.Config;
import com.pk.mdmc.core.IMessageProducer;
import com.pk.mdmc.core.Message;

/**
 * Created by PavelK on 4/13/2016.
 */
public class MessageProducerImpl implements IMessageProducer {
    Config cnfg;

    public MessageProducerImpl(Config cnfg){
        this.cnfg = cnfg;
    }

    boolean TRACE = true;
    @Override
    public Message next() {
        Message msg = new Message(cnfg);
        msg.init(-1);
        //if (TRACE) System.out.println("RECEIVER> next ");
        return msg;
    }

    @Override
    public void push(Message msg) {
        if (msg.isFilled()) {
            if (TRACE) System.out.println("   RECEIVER  >>>  push "+msg);
        } else {
            if (TRACE) System.out.println("   RECEIVER  >>>  drop "+msg);
        }
        System.out.println();
    }
}
