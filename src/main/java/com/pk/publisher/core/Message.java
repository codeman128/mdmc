package com.pk.publisher.core;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Message {
    public enum TYPE {UNKNOWN, SNAPSHOT, UPDATE, HEARTBEAT}
    public TYPE type = TYPE.UNKNOWN;
    protected final byte[] buffer;
    protected long dSequence;
    public int length = 0;
    public long eventTime; //arb time

    private Message(){
        buffer = null;
    }

    public Message(int size) {
        buffer = new byte[size];
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void init(long dSequence) {
        type = TYPE.UNKNOWN;
        this.dSequence = dSequence;
        length = 0;
        eventTime = 0;
    }

    public long getDSequence(){
        return dSequence;
    }

}
