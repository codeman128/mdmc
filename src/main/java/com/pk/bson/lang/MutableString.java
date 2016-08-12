package com.pk.bson.lang;

/**
 * Created by PavelK on 8/7/2016.
 */
public class MutableString extends ImmutableString {

    public MutableString(String string) {
        super(string);
    }

    public MutableString(MutableString mStr){
        super(mStr);
    }

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

    public void set(byte[] buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset = offset;
        if (length>=0) {
            this.length = length;
        } else this.length = buffer.length;
        hashCode();
    }

    public byte[] getBuffer(){
        return buffer;
    }

    public void setBuffer(byte[] buffer){
        this.buffer = buffer;
        hashCode();
    }

    public void setOffset(int offset) {
        this.offset = offset;
        hashCode();
    }

    public void setLength(int length) {
        this.length = length;
        hashCode();
    }

    public void copyFrom(MutableString source) { //please note - not allows growth
        super.copyFrom(source);
    }

}
