package com.pk.mdmc;

import com.pk.mdmc.IConfig;
import com.pk.mdmc.Packet;

/**
 * Created by PavelK on 3/26/2016.
 */
public class Message {
    private long sid;
    private short packetsExpected;
    private short packetsReceived;
    private final Packet[] packets;
    private final boolean[] isReceived;
    private final boolean TRACE;
    private long rbSequence;

    private Message() {
        packets = null;
        isReceived = null;
        TRACE = true;
    }

    public Message(IConfig cnfg){
        TRACE = cnfg.getNetTraceEnabled();
        packets = new Packet[cnfg.getMsgMaxPackets()];
        isReceived = new boolean[cnfg.getMsgMaxPackets()];
        for (int i=0; i<packets.length; i++) {
            packets[i] = new Packet(cnfg);
        }

        init(-1);// -1? ot min int?
    }

    public void init(long rbSequence) {
        sid = 0;
        packetsExpected = 0;
        packetsReceived = 0;
        this.rbSequence = rbSequence;
        for (int i=0; i<packets.length; i++) {
            isReceived[i]= false;
        }
    }

    public Packet addPacket(final Packet packet) {
        final short segment = packet.getSegmentId();
        if (!isReceived[segment]) {
            // set segment id (first packet after init)
            if (packetsExpected ==0) {
                sid=packet.getSequenceId();
                packetsExpected = packet.getSegmentCount();
            }
            // mark segment as received
            isReceived[segment] = true;
            packetsReceived++;
            // swap packets
            final Packet oldPacket = packets[segment];
            packets[segment] = packet;
            return oldPacket;
        } else {
             // duplicate packet received - ignore todo log trace
            return packet;
        }
    }

    public boolean isFilled() {
        return (packetsExpected !=0 && packetsExpected == packetsReceived);
    }

    public String toString(){
        String state;
        if (packetsExpected==0) { state = "INIT"; } else
            if (packetsReceived==0) { state = "EMPTY";} else
                if (isFilled()) { state="FILLED ";} else
                    state = "PARTIAL";


        StringBuilder sb = new StringBuilder(127);
        sb.append("Message [").append(sid).append("] ").
                append(state).append(" ").append(packetsReceived).append("-").append(packetsExpected);
        return sb.toString();
    }

    public long getRbSequence() {
        return rbSequence;
    }

    public long getSid() {
        return sid;
    }

    public short getPacketsExpected() {
        return packetsExpected;
    }

    public short getPacketsReceived() {
        return packetsReceived;
    }
}
