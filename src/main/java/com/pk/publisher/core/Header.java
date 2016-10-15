package com.pk.publisher.core;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by PavelK on 6/13/2016.
 */
public class Header {
    private final int[] headerPrefixSize = new int[4];
    private final byte[][] buffers = new byte[4][];
    private final IPublisherConfig config;

    private void addHeader(Message.TYPE type, String header){
        headerPrefixSize[type.getId()] = header.length();
        buffers[type.getId()] = header.getBytes();
    }

    private Header(){
        config = null;
    }

    public Header(IPublisherConfig config){
        this.config = config;
        addHeader(Message.TYPE.SNAPSHOT,  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><rates_update type=\"snapshot\"><sequence_number>                    ");
        addHeader(Message.TYPE.UPDATE,    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><rates_update type=\"update\"><sequence_number>                    ");
        addHeader(Message.TYPE.HEARTBEAT, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><rates_update type=\"heartbeat\"><sequence_number>                    ");
    }

    public final int addHeaderAndWrite(final OutputStream stream, final Message msg, final long msgSeqId) throws IOException {
        final int msgTypeId = msg.type.getId();
        final byte[] headerBuffer = buffers[msgTypeId];
        final int prefixSize = headerPrefixSize[msgTypeId];

        final int headerLen = config.addMsgSeqId(headerBuffer, prefixSize, msgSeqId);

        final int newMsgLen = msg.length + headerLen;
        final int newMsgOff = msg.offset-headerLen;

        // copy header to the messages from disruptor, overriding/modifying previous one  BUGGGGG!!!!!!!
        System.arraycopy(headerBuffer, 0, msg.buffer, newMsgOff, headerLen);

        // write message buffer with header to a socket
        stream.write(msg.buffer, newMsgOff, newMsgLen);
        return newMsgLen;
    }
}
