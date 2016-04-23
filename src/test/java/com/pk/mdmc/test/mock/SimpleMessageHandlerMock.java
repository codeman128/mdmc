package com.pk.mdmc.test.mock;


import com.pk.mdmc.IConfig;
import com.pk.mdmc.client.IMessageBuffer;
import com.pk.mdmc.client.Message;

/**
 * Created by PavelK on 4/13/2016.
 */
public class SimpleMessageHandlerMock implements IMessageBuffer {
    IConfig cnfg;

    public SimpleMessageHandlerMock(IConfig cnfg){
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
