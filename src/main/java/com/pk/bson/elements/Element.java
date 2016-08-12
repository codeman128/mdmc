package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.Document;
import com.pk.bson.MutableString;

/**
 * Created by pkapovski on 8/4/2016.
 */
public abstract class Element {
    private static final byte T_EOO     = 0x00;
    private static final byte T_DOUBLE  = 0x01;
    private static final byte T_STRING  = 0x02;
    private static final byte T_OBJECT  = 0x03;
    private static final byte T_ARRAY   = 0x04;
    private static final byte T_BOOLEAN = 0x08;
    private static final byte T_INT32   = 0x10;


    public enum TYPE {
        EOF(1),
        EOO(T_EOO),
        DOUBLE(T_DOUBLE),
        STRING(T_STRING),
        EMBEDDED(T_OBJECT),
        ARRAY(T_ARRAY),
//        BINARY(5),
        UNDEFINED(6),
//        OBJECT_ID(7),
        BOOLEAN(T_BOOLEAN),
//        UTC_DATE_TIME(9),
        //NULL(10),
//        REGULAR_EXPRESSION(11),
//        DB_POINTER(12),
//        JAVASCRIPT_CODE(13),
//        SYMBOL(14),
//        JAVASCRIPT_CODE_WITH_SCOPE(15),
        INT32(T_INT32),
//        TIMESTAMP(17),
//        INT64(18),
//        MAX_KEY(127),
        MIN_KEY(255);

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

    private MutableString name;

    private Element(){}

    protected Element(MutableString name) {
        this.name = name;
    }

    public MutableString getName(){
        return name;
    }

    protected abstract void read(BsonStream stream);

     public static Element createElement(Element.TYPE type, MutableString name) {
        switch (type){
            case STRING   : return new StringElement(name);
            case INT32    : return new INT32Element(name);
            case EMBEDDED : return new Document(name);
            case ARRAY    : return new ArrayElement(name);
            case BOOLEAN  : return new BooleanElement(name);
            case DOUBLE   : return new DoubleElement(name);
            default       : return null;
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

}
