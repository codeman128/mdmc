package com.pk.mdmc.test.mock;

import com.lmax.disruptor.EventHandler;
import com.pk.mdmc.core.Message;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 4/13/2016.
 */
public class DisrupterMessageHandlerMock implements EventHandler<Message> {

    List<Message> list = new ArrayList<Message>(32);

    public void onEvent(Message event, long sequence, boolean endOfBatch) {
        list.add(event);
        System.out.println("["+sequence+"] >>>>>>>>>>> "+event);
    }

    public List<Message> getResults(){
        return list;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(1024);
        sb.append(list.size()).append(" messages received:\n");
        for (Message msg : list) {
            sb.append(msg).append('\n');
        }
        return sb.toString();
    }

    public void reset(){
        list.clear();
    }
}