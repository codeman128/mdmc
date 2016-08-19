package com.pk.bson.serialization;

import com.pk.bson.core.Collection;
import com.pk.bson.lang.MutableString;

import java.nio.ByteBuffer;

/**
 * Created by PavelK on 8/19/2016.
 */
public class JsonStream {
    private final MutableString str = new MutableString(1024);

    private enum JsonElementType {UNKNOWN, STRING, INTEGER, DOUBLE, FALSE, TRUE, NULL}
    private final byte[] buffer = new byte[1024*10];
    private int bufLength = 0;
    private ByteBuffer bb;



    private byte readByteIgnoreSpaces(){
        byte b = ' ';
        while(b==' '){
            b = bb.get();
        }
        return b;
    }

    private JsonElementType readElement(){
        // init
        bufLength = 0;
         byte b = readByteIgnoreSpaces();
        switch (b) {
            case '"': { // read String
                while (true) {
                    buffer[bufLength] = bb.get();
                    //handle escape chars
                    if (buffer[bufLength]=='\\') {
                        buffer[++bufLength] =bb.get();
                    }
                    if (buffer[bufLength]=='"') {
                        return JsonElementType.STRING;
                    } else {
                        bufLength++;
                    }
                }
                //return JsonElementType.UNKNOWN;
            }
            case 'f': { //read false
                if (bb.get()=='a' && bb.get()=='l' && bb.get()=='s' && bb.get()=='e') {
                    return JsonElementType.FALSE;
                } else return JsonElementType.UNKNOWN;
            }
            case 't': { //read true
                if (bb.get()=='r' && bb.get()=='u' && bb.get()=='e') {
                    return JsonElementType.TRUE;
                } else return JsonElementType.UNKNOWN;
            }
            case 'n': { //read null
                if (bb.get()=='u' && bb.get()=='l' && bb.get()=='l') {
                    return JsonElementType.NULL;
                } else return JsonElementType.UNKNOWN;
            }
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': { //number
                buffer[bufLength++]=b;
                boolean pointFound = false;
                while (true){
                    b=bb.get();
                    if (('0'<=b && b<='9')||(b=='e')||(b=='.'&& !pointFound)) {
                        buffer[bufLength++]=b;
                        if (b=='.') pointFound = true;
                    } else {
                        bb.position(bb.position()-1);
                        if (pointFound) {
                            return JsonElementType.DOUBLE;
                        } else return JsonElementType.INTEGER;
                    }
                }

            }
        }
        return JsonElementType.UNKNOWN;
    }

    public boolean objectStart() {
        byte b = readByteIgnoreSpaces();
        return b=='{';
    }

    public void init (ByteBuffer byteStreams) {
        bb = byteStreams;
    }

    public void readCollectionFromBSON(Collection collection) {
        JsonElementType type = readElement();
        if (type!= JsonElementType.UNKNOWN){
            System.out.println(type+" > ["+new String(buffer,0, bufLength)+"]");
        } else{
            System.out.println(type);
        }

        byte s = readByteIgnoreSpaces();
        if (s==':') {
            System.out.println(" : ");
        } else {
            System.out.println("no : instead "+(char)s);
        }

        type = readElement();
        if (type!= JsonElementType.UNKNOWN){
            System.out.println(type+" > ["+new String(buffer,0, bufLength)+"]");
        } else{
            System.out.println(type);
        }


    }
}
