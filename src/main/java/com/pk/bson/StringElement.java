package com.pk.bson;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class StringElement extends Element {
    private byte[] value;
    private int length;

    StringElement(MutableString name) {
        super(name);
    }

    @Override
    void read(BsonStream stream) {
        setValue(stream.getBuffer(), 0, stream.readString());
    }

    public void setValue(byte[] value, int valOffset, int valLength) {
        if (this.value==null || this.value.length<valLength) {
            this.value = new byte[valLength];
        }
        System.arraycopy(value, valOffset, this.value, 0, valLength);
        this.length = valLength;
    }

    public String toString(){
        return new String(value, 0, length);
    }

}
