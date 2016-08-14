package com.pk.bson;

import com.pk.bson.elements.Element;
import com.pk.bson.elements.Record;
import com.pk.bson.lang.ImmutableString;
import com.pk.bson.lang.MutableString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class BsonStream {
    private final MutableString str = new MutableString(1024);
    private ByteBuffer bb;

    public void init (ByteBuffer byteStreams) {
        bb = byteStreams;
        bb.order(ByteOrder.LITTLE_ENDIAN);
    }

    public final ImmutableString readKey() {
        int length = -1;
        str.setOffset(0);
        while (true) {
            if ((str.getBuffer()[++length] = bb.get())==0) break;
        }
        str.setLength(length);

        return str;
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

    public final Record.TYPE readNextType(){
        return Record.getType(bb.get());
    }

    public double getDouble() {
        return bb.getDouble();
    }

    public boolean readBoolean(){
        byte b = bb.get();
        return (b == 1);

    }
}
