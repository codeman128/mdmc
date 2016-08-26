package com.pk.redis;

import com.pk.lang.ImmutableString;
import com.pk.lang.MutableString;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by PavelK on 8/26/2016.
 */
public class RedisStringBuilder {
    protected final MutableString mString;

    public RedisStringBuilder(int length) {
        mString = new MutableString(length);
    }

    public String toString(){
        return mString.toString();
    }

    protected RedisStringBuilder appendRN() {
        mString.append((byte)'\r').append((byte)'\n');
        return this;
    }

    public RedisStringBuilder appendString(ImmutableString string){
        mString.append((byte)'$').append(string.getLength());
        appendRN();
        mString.append(string);
        appendRN();
        return this;
    }

    public RedisStringBuilder appendCommand(Commands command, int paramCount) {
        mString.reset();
        mString.append((byte)'*');
        mString.append(paramCount+1);
        appendRN();
        appendString(command.getString());
        return this;
    }

    public void write (DataOutput out) throws IOException {
        mString.write(out);
    }
}
