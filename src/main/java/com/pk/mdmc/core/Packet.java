package com.pk.mdmc.core;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Created by PavelK on 3/26/2016.
 */
public class Packet {

    private final byte[] buffer;
    private final ByteBuffer bb;
    private final DatagramPacket datagram;

    private final static int OFFSET_SEQUENCE_ID   = 0;
    private final static int OFFSET_SEGMENT_ID    = OFFSET_SEQUENCE_ID + 8;
    private final static int OFFSET_SEGMENT_COUNT = OFFSET_SEGMENT_ID + 2;
    private final static int OFFSET_PAYLOAD_SIZE  = OFFSET_SEGMENT_COUNT + 2;
    private final static int OFFSET_PAYLOAD       = OFFSET_PAYLOAD_SIZE + 2;

    private Packet() {
        buffer = null;
        bb = null;
        datagram = null;
    }

    public Packet(IConfig cnfg) {
        final int size = cnfg.getPacketMaxSize();
        buffer = new byte[size];
        bb = ByteBuffer.wrap(buffer, 0, size);
        datagram = new DatagramPacket(buffer, 0, size);
        datagram.setAddress(cnfg.getAddress());
        datagram.setPort(cnfg.getPort());
    }

    public void setSequenceId(long id) { bb.putLong(OFFSET_SEQUENCE_ID, id); }
    public long getSequenceId() { return bb.getLong(OFFSET_SEQUENCE_ID);     }

    public void  setSegmentId(short id) { bb.putShort(OFFSET_SEGMENT_ID, id); }
    public short getSegmentId() {  return bb.getShort(OFFSET_SEGMENT_ID);     }

    public void  setSegmentCount(short count) { bb.putShort(OFFSET_SEGMENT_COUNT, count); }
    public short getSegmentCount() {     return bb.getShort(OFFSET_SEGMENT_COUNT);        }

    public void  setPayloadSize(short size) { bb.putShort(OFFSET_PAYLOAD_SIZE, size);
        datagram.setLength(OFFSET_PAYLOAD_SIZE+2+size);  }
    public short getPayloadSize() {    return bb.getShort(OFFSET_PAYLOAD_SIZE);       }

    public DatagramPacket getDatagram() {
        return datagram;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(120);
        sb.append("Packet [").append(getSequenceId()).append("] ").
                append(getSegmentId()).append("-").append(getSegmentCount()).
                append(" ").append(getPayloadSize()).append(" bytes.");
        return sb.toString();
    }
}
