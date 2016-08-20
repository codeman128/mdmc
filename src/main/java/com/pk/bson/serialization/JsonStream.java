package com.pk.bson.serialization;

import com.pk.bson.core.Collection;
import com.pk.bson.lang.MutableString;
import com.pk.bson.lang.NumberUtils;

import java.nio.ByteBuffer;

/**
 * Created by PavelK on 8/19/2016.
 */
public class JsonStream {
    private final MutableString str = new MutableString(1024);

    private enum JsonElementType {UNKNOWN, STRING, INTEGER, DOUBLE, FALSE, TRUE, NULL, OBJECT, OBJECT_END, ARRAY_END, COMMA, ARRAY}
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
            case '"': { // read String ---------------------------------------------------------------------------------
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
            }
            case 'f': { //read false -----------------------------------------------------------------------------------
                if (bb.get()=='a' && bb.get()=='l' && bb.get()=='s' && bb.get()=='e') {
                    return JsonElementType.FALSE;
                } else return JsonElementType.UNKNOWN;
            }
            case 't': { //read true ------------------------------------------------------------------------------------
                if (bb.get()=='r' && bb.get()=='u' && bb.get()=='e') {
                    return JsonElementType.TRUE;
                } else return JsonElementType.UNKNOWN;
            }
            case 'n': { //read null ------------------------------------------------------------------------------------
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
            case '9': { //read number ---------------------------------------------------------------------------------
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
            case '{': return JsonElementType.OBJECT; //read object -----------------------------------------------------
            case '}': return JsonElementType.OBJECT_END;
            case '[': return JsonElementType.ARRAY; //read array -------------------------------------------------------
            case ']': return JsonElementType.ARRAY_END;
            case ',': return JsonElementType.COMMA;
        }
        return JsonElementType.UNKNOWN;
    }

    protected boolean readKeyValuePair(){
        if (readElement()==JsonElementType.STRING) {
            str.copyFrom(buffer, 0, bufLength);
            if (readByteIgnoreSpaces()==':') {
                JsonElementType type = readElement();
                System.out.println("["+str+"]:["+new String(buffer, 0 , bufLength)+"] "+type);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean objectStart() {
        byte b = readByteIgnoreSpaces();
        return b=='{';
    }

    public void init (ByteBuffer byteStreams) {
        bb = byteStreams;
    }

//    private void setBoolean(Collection collection, boolean value){
//        if (collection.getType()== Collection.TYPE.OBJECT) {
//            collection.setBoolean(str, value);
//        } else {
//            collection.addBoolean(value);
//        }
//    }

    private void readArrayFromBSON(Collection collection) throws Exception {
        while (true) {
            switch (readElement()) {
                case TRUE: {
                    collection.addBoolean(true);
                    break;
                }
                case FALSE: {
                    collection.addBoolean(false);
                    break;
                }
                case INTEGER: {
                    collection.addInt(NumberUtils.toInt(buffer, 0, bufLength));
                    break;
                }
                case OBJECT: {
                    readCollectionFromBSON((Collection) collection.setObject(str));
                    break;
                }
                case ARRAY: {
                    readArrayFromBSON((Collection) collection.setArray(str));
                    break;
                }
                case ARRAY_END: {
                    return;
                }
                case COMMA: {
                    //do nothing
                }
            }
        }
    }

    public void readCollectionFromBSON(Collection collection) throws Exception {
        while (true){
            switch (readElement()) {
                case OBJECT_END: return;
                case STRING:{
                    str.copyFrom(buffer, 0, bufLength);
                    if (readByteIgnoreSpaces()==':') {
                        switch (readElement()){
                            case TRUE: {
                                collection.setBoolean(str, true);
                                break;
                            }
                            case FALSE: {
                                collection.setBoolean(str, false);
                                break;
                            }
                            case INTEGER: {
                                collection.setInt(str, NumberUtils.toInt(buffer, 0, bufLength));
                                break;
                            }
                            case OBJECT: {
                                readCollectionFromBSON((Collection) collection.setObject(str));
                                break;
                            }
                            case ARRAY: {
                                readArrayFromBSON((Collection) collection.setArray(str));
                            }
                        }
                    } else {
                        throw new Exception("invalid format ");
                    }
                }
            }
        }



    }
}
