package com.pk.publisher;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Message {
    public enum TYPE {UNKNOWN, SNAPSHOT, UPDATE, HEARTBEAT}
    public TYPE type;
    protected final byte[] data;
    public int length = 0;

    private Message(){
        data = null;
    }

    public Message(int size) {
        data = new byte[size];
    }

    public byte[] getData(){
        return data;
    }


}
