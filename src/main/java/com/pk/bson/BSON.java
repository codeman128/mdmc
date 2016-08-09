package com.pk.bson;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class BSON {
    static final byte T_EOO    = 0x00;
    static final byte T_STRING = 0x02;
    static final byte T_OBJECT = 0x03;
    static final byte T_ARRAY  = 0x04;
    static final byte T_INT32  = 0x10;


    enum TYPE {
        EOF(1),
        EOO(T_EOO),
        DOUBLE(1),
        STRING(T_STRING),
        EMBEDDED(T_OBJECT),
        ARRAY(T_ARRAY),
        BINARY(5),
        UNDEFINED(6),
        OBJECT_ID(7),
        BOOLEAN(8),
        FALSE(0),
        TRUE(1),
        UTC_DATE_TIME(9),
        NULL(10),
        REGULAR_EXPRESSION(11),
        DB_POINTER(12),
        JAVASCRIPT_CODE(13),
        SYMBOL(14),
        JAVASCRIPT_CODE_WITH_SCOPE(15),
        INT32(T_INT32),
        TIMESTAMP(17),
        INT64(18),
        MAX_KEY(127),
        MIN_KEY(255);

        public final byte id;

        TYPE(int id){
            this.id = (byte)id;
        }

    }

    enum SUBTYPE {
        GENERIC(0),
        FUNCTION(1),
        OLD(2),
        UUID(3),
        MD5(5),
        USER(128);

        public final byte id;

        SUBTYPE(int id){
            this.id = (byte)id;
        }
    }
}
