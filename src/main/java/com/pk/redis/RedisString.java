package com.pk.redis;

import com.pk.bson.lang.ImmutableString;
import com.pk.bson.lang.MutableString;

/**
 * Created by PavelK on 8/26/2016.
 */
public class RedisString {
    protected final MutableString mString;

    public RedisString(int length) {
        mString = new MutableString(length);
    }

    public String toString(){
        return mString.toString();
    }

    protected RedisString appendRN() {
        mString.append((byte)'\r').append((byte)'\n');
        return this;
    }

    public RedisString appendString(ImmutableString string){
        mString.append((byte)'$').append(string.getLength());
        appendRN();
        mString.append(string);
        appendRN();
        return this;
    }
}
