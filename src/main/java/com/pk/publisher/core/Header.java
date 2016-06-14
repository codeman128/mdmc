package com.pk.publisher.core;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by PavelK on 6/13/2016.
 */
public class Header {
    private final String RESERVED = "                    ";
    private final int[] size = new int[4];
    private final byte[][] buffer = new byte[4][];
    private final IPublisherConfig config;

    private void addHeader(Message.TYPE type, String header){
        size[type.getId()] = header.length();
        buffer[type.getId()] = (header + RESERVED).getBytes();
    }

    private Header(){
        config = null;
    }

    public Header(IPublisherConfig config){
        this.config = config;
        addHeader(Message.TYPE.SNAPSHOT, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><rates_update type=\"snapshot\"><sequence_number>");
        addHeader(Message.TYPE.UPDATE,   "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><rates_update type=\"update\"><sequence_number>");
        addHeader(Message.TYPE.HEARTBEAT,"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><rates_update type=\"heartbeat\"><sequence_number>");
    }

    public final int addHeaderAndWrite(OutputStream stream, Message msg, long msgSeqId) throws IOException {
        final byte[] b = buffer[msg.type.getId()];
        final int newLength = config.addMsgSeqId(b, size[msg.type.getId()], msgSeqId);
        System.arraycopy(b, 0, msg.buffer, msg.offset-newLength, newLength);
        stream.write(msg.getBuffer(), msg.offset - newLength, msg.length + newLength);
        return msg.length;
    }
}
