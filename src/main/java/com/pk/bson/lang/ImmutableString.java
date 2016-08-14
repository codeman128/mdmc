package com.pk.bson.lang;

/**
 * Created by PavelK on 8/12/2016.
 */
public class ImmutableString {
    protected byte[] buffer;
    protected int offset;
    protected int length;
    protected int hash;

    protected ImmutableString(){}

    public ImmutableString(ImmutableString mStr){
        buffer = new byte[mStr.length];
        copyFrom(mStr);
    }

    public ImmutableString(String str) {
        byte[] source = str.getBytes();
        length = source.length;
        offset = 0;
        buffer = new byte[length];
        System.arraycopy(source, 0, buffer, 0, length);
        hashCode();
    }

    public int getOffset(){
        return offset;
    }

    public int getLength() {
        return length;
    }

    public String toString(){
        return new String(buffer, 0, length);
    }

    public int hashCode() {
        if (buffer== null || length<=0) {
            hash =0;
            return hash;
        }

        hash = 1;
        for (int i=offset; i<offset+length; i++) {
            hash = 31*hash + buffer[i];

        }
        return hash;
    }

    protected void copyFrom(ImmutableString source) { //please note - not allows growth
        offset = 0;
        length = source.length;
        System.arraycopy(source.buffer, source.offset, buffer, offset, length);
        hashCode();
    }

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof ImmutableString) {
            ImmutableString anotherStr = (ImmutableString)anObject;

            if (length == anotherStr.length) {
                for (int i=0; i<length; i++) {
                    if (buffer[offset+i]!=anotherStr.buffer[anotherStr.offset+i]) return false;
                }
                return true;
            }
        }
        return false;
    }
}
