package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.ImmutableString;
import com.pk.bson.lang.MutableString;
import com.pk.bson.lang.StringDictionary;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class Element {

    private static final byte T_EOO       = 0x00;
    private static final byte T_DOUBLE    = 0x01;
    private static final byte T_STRING    = 0x02;
    private static final byte T_OBJECT    = 0x03;
    private static final byte T_ARRAY     = 0x04;
    private static final byte T_UNDEFINED = 0x06;
    private static final byte T_BOOLEAN   = 0x08;
    private static final byte T_INT32     = 0x10;

    public enum TYPE {
        EOF(1),
        EOO(T_EOO),
        DOUBLE(T_DOUBLE),
        STRING(T_STRING),
        EMBEDDED(T_OBJECT),
        ARRAY(T_ARRAY),
        UNDEFINED(T_UNDEFINED),
        BOOLEAN(T_BOOLEAN),
        INT32(T_INT32);
        // BINARY(5),
        // OBJECT_ID(7),
        // UTC_DATE_TIME(9),
        // NULL(10),
        // REGULAR_EXPRESSION(11),
        // DB_POINTER(12),
        // JAVASCRIPT_CODE(13),
        // SYMBOL(14),
        // JAVASCRIPT_CODE_WITH_SCOPE(15),
        // TIMESTAMP(17),
        // INT64(18),
        // MAX_KEY(127)
        // MIN_KEY(255)
        public final byte id;
        TYPE(int id){
            this.id = (byte)id;
        }
    }
//
//    enum SUBTYPE {
//        GENERIC(0),
//        FUNCTION(1),
//        OLD(2),
//        UUID(3),
//        MD5(5),
//        USER(128);
//
//        public final byte id;
//
//        SUBTYPE(int id){
//            this.id = (byte)id;
//        }
//    }

    protected int key;   // reference to the dictionary
    protected TYPE type;
    protected long data;
    Object reference;  // to string or container
    protected Element previous;
    protected Element next;


    Element() {
        type = TYPE.UNDEFINED;
    }

     final void init(TYPE type, int key) {
         this.type = type;
         this.key = key;
         this.next = null;
         this.previous = null;
    }

    public final int getKey(){
        return key;
    }

    public final TYPE getType(){
        return type;
    }

    public Element getPrevious() {
        return previous;
    }

    void setPrevious(Element previous) {
        this.previous = previous;
    }

    public Element getNext() {
        return next;
    }

    void setNext(Element next) {
        this.next = next;
    }

    public void read(BsonStream stream) throws NoSuchFieldException {
        switch (type) {
            case INT32: {
                setInt(stream.getInt32());
                return;
            }
            case BOOLEAN: {
                setBoolean(stream.readBoolean());
                return;
            }
            case DOUBLE: {
                setDouble(stream.getDouble());
                return;
            }
            case STRING: {
                MutableString str = new MutableString(200);
                stream.readString(str);
                reference = str;
                return;
            }
            case EMBEDDED : {
                ((Collection)reference).read(stream);
                return;
            }
            case ARRAY: {
                ((Collection)reference).read(stream);
                return;
            }
        }
    }

    public void setDouble(double value) {
        releaseReference();
        type = TYPE.DOUBLE;
        data = Double.doubleToRawLongBits(value);
    }

    public double getDouble()throws NoSuchFieldException {
        if (type!= TYPE.DOUBLE) throw new NoSuchFieldException();
        return Double.longBitsToDouble(data);
    }

    public void setBoolean(boolean value){
        releaseReference();
        type = TYPE.BOOLEAN;
        data = (value) ? 1 : 0;
    }

    public boolean getBoolean() throws NoSuchFieldException {
        if (type != TYPE.BOOLEAN) throw new NoSuchFieldException();
        return (data == 1);
    }

    public void setInt(int value) {
        releaseReference();
        type = TYPE.INT32;
        data = (long)value;
    }

    public int getInt() throws NoSuchFieldException {
        if (type != TYPE.INT32) throw new NoSuchFieldException();
        return (int)data;
    }

    public IObject setObject(CollectionCache collectionCache) {
        if (type!=TYPE.EMBEDDED) {
            releaseReference();
            type = TYPE.EMBEDDED;
            reference = collectionCache.acquier(Collection.TYPE.OBJECT);
        }
        return (IObject)reference;
    }

    public IObject getObject()throws NoSuchFieldException {
        if (type != TYPE.EMBEDDED) throw new NoSuchFieldException();
        return  (IObject)reference;
    }

    public IArray setArray(CollectionCache collectionCache) {
        if (type!=TYPE.ARRAY) {
            releaseReference();
            type = TYPE.ARRAY;
            reference = collectionCache.acquier(Collection.TYPE.ARRAY);
        }
        return (IArray)reference;
    }


    public IArray getArray()throws NoSuchFieldException {
        if (type != TYPE.ARRAY) throw new NoSuchFieldException(); //todo invalid type exception
        return  (IArray)reference;
    }

    public String toString() {
        try {
            switch (type) {
                case INT32: {
                    return Integer.toString(getInt());
                }
                case BOOLEAN: {
                    return Boolean.toString(getBoolean());
                }
                case DOUBLE: {
                    return Double.toString(getDouble());
                }
                case STRING: {
                    return "\""+(ImmutableString)reference+"\"";
                }
                case EMBEDDED : {
                    return reference.toString();
                }
                case ARRAY: {
                    return reference.toString();
                }
                default: {
                    return "???";
                }
            }
        } catch (NoSuchFieldException e) {
            return e.toString();
        }
    }

    public static TYPE getType(byte type) {
        switch (type) {
            case T_EOO     : return TYPE.EOO;
            case T_DOUBLE  : return TYPE.DOUBLE;
            case T_STRING  : return TYPE.STRING;
            case T_OBJECT  : return TYPE.EMBEDDED;
            case T_ARRAY   : return TYPE.ARRAY;
            case T_INT32   : return TYPE.INT32;
            case T_BOOLEAN : return TYPE.BOOLEAN;
            default        : return TYPE.UNDEFINED;
        }
    }

    void releaseReference() {
        if (reference!=null) {
            if (type==TYPE.EMBEDDED || type==TYPE.ARRAY) {
                ((Collection)reference).release();
            }
            if (type==TYPE.STRING) {
                //todo release "long" strings
            }
            reference = null;
        }
    }


}
