package com.pk.bson;

/**
 * Created by PavelK on 8/7/2016.
 */
public class MutableString {
    private byte[] buffer;
    private int offset;
    private int length;
    private int hash;

    public MutableString(int length){
        buffer = new byte[length];
        this.length = 0;
    }

    public MutableString(byte[] buffer, int offset, int length){
        set(buffer, offset, length);
    }

    public MutableString(byte[] buffer) {
        set(buffer, 0, buffer.length);
    }

    public MutableString(String string) {
        set(string.getBytes(), 0, -1);
    }

    public void set(byte[] buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset = offset;
        if (length>=0) {
            this.length = length;
        } else this.length = buffer.length;
        hashCode();
    }

    public int hashCode() {
        if (buffer!= null && hash == 0 && length > 0) {
            hash = 1;
            for (int i=0; i<length; i++) {
                hash = 31*hash + buffer[i];
            }
        } else {
            hash = 0;
        }
        return hash;
    }

    public byte[] getBuffer(){
        return buffer;
    }

    public void setBuffer(byte[] buffer){
        this.buffer = buffer;
        hashCode();
    }

    public int getOffset(){
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
        hashCode();
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
        hashCode();
    }

    public String toString(){
        return new String(buffer, 0, length);
    }

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof MutableString) {
            MutableString anotherStr = (MutableString)anObject;

            if (length == anotherStr.length) {
                for (int i=0; i<length; i++) {
                    if (buffer[offset+i]!=anotherStr.buffer[anotherStr.offset+i]) return false;
                }
                return true;
            }
        }
        return false;
    }

    public void cloneFrom(MutableString source) { //please note - not allows growth
        offset = 0;
        length = source.length;
        System.arraycopy(source.buffer, source.offset, buffer, offset, length);
    }

}
