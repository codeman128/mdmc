package com.pk.bson;

import com.pk.bson.elements.Element;
import com.pk.bson.lang.MutableString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class BsonStream {
    byte[] buf = new byte[1024*10];
    private final ByteBuffer bb;

    private BsonStream(){
        bb = null;
    }

    public BsonStream (ByteBuffer byteStreams) {
        bb = byteStreams;
        bb.order(ByteOrder.LITTLE_ENDIAN);
    }

    public final void readKey(MutableString mStr) {
        int length = -1;
        mStr.setOffset(0);
        while (true) {
            if ((mStr.getBuffer()[++length] = bb.get())==0) break;
        }
        mStr.setLength(length);
    }

    public final void readString(MutableString mStr) {
        int length = bb.getInt()-1;
        bb.get(mStr.getBuffer(), 0, length);
        bb.get();
        mStr.setOffset(0);
        mStr.setLength(length);
    }

    public final int getInt32(){
       return bb.getInt();
    }

    public final Element.TYPE readNextType(){
        return Element.getType(bb.get());
    }

    byte[] getBuffer(){
        return buf;
    }

    public int position() {
        return bb.position();
    }

    public double getDouble() {
        return bb.getDouble();
    }

    public boolean readBoolean(){
        byte b = bb.get();
        return (b == 1);

    }
}
