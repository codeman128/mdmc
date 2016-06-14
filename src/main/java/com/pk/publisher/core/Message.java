package com.pk.publisher.core;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Message {
    public enum TYPE {UNKNOWN(0), SNAPSHOT(1), UPDATE(2), HEARTBEAT(3);
        final int id;

        TYPE(int id) {
            this.id = id;
        }

        public int getId(){
            return id;
        }

    }
    public TYPE type = TYPE.UNKNOWN;
    protected final byte[] buffer;
    protected long dSequence;
    public int offset = 0;
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
