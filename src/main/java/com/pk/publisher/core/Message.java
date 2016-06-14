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

    private static final byte[] MSG_PREFIX = "</sequence_number><msg_date>2000-00-00</msg_date><msg_time>00:00:00.000</msg_time>".getBytes();

    public TYPE type = TYPE.UNKNOWN;
    protected final byte[] buffer;
    protected long dSequence;
    public final int offset = 125;
    public int length = 0;
    public long eventTime; //arb time

    private Message(){
        buffer = null;
    }

    public Message(int size) {
        buffer = new byte[size];
        System.arraycopy(MSG_PREFIX, 0, buffer, offset, MSG_PREFIX.length);
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
