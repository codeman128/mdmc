package com.pk.mdmc.test.utils;


import com.pk.mdmc.client.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 4/16/2016.
 */
public class ExpectedResults {
    public class MessageMock {
        long seqId;
        short packetsExpected;
        short packetsReceived;

        public MessageMock (long seqId, int packetsReceived, int packetsExpected) {
            this.seqId = seqId;
            this.packetsExpected = (short)packetsExpected;
            this.packetsReceived = (short)packetsReceived;
        }

        public boolean compare(Message msg) {
            return (msg.getSid()==seqId)&&
                    (msg.getPacketsExpected()==packetsExpected)&&
                    (msg.getPacketsReceived()==packetsReceived);
        }
    }

    List<MessageMock> list = new ArrayList<MessageMock>(1000);

    public ExpectedResults addMessage(long seqId, int packetsReceived, int packetsExpected) {
        list.add(new MessageMock(seqId, packetsReceived, packetsExpected));
        return this;
    }

    public boolean compare(List<Message> results) {
        if (results.size()!=list.size()) return false;
        for (int i=0; i<list.size(); i++) {
            if (!(list.get(i).compare(results.get(i)))) return false;
        }
        return true;
    }
}
