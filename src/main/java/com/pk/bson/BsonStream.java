package com.pk.bson;

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

    public final int readKey() {
        int length = -1;
        while (true) {
            if ((buf[++length] = bb.get())==0) break;
        }
        return length;
    }

    public final int readString() {
        int length = bb.getInt()-1;
        bb.get(buf, 0, length);
        bb.get();
        System.out.println(new String(buf,0,length));
        return length;
    }

//    public final int readString2(byte[] strBuffer) {
//        int length = bb.getInt()-1;
//        System.arraycopy(bb.array(), bb.arrayOffset(), buf, 0, length);
//        bb.get(); //read Null termination
//        return length;
//    }

    public final int getINT32(){
       return bb.getInt();
    }

    public final BSON.TYPE getType(){
        byte b = bb.get();
        switch (b) {
            case BSON.T_EOO:    return BSON.TYPE.EOO;
            case BSON.T_STRING: return BSON.TYPE.STRING;
            case BSON.T_OBJECT: return BSON.TYPE.EMBEDDED;
            case BSON.T_INT32:  return BSON.TYPE.INT32;

        }
        return BSON.TYPE.UNDEFINED;
    }



    byte[] getBuffer(){
        return buf;
    }
}
