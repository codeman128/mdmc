package com.pk.bson;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class INT32Element extends Element {
    int value;

    INT32Element(MutableString name) {
        super(name);
    }

    @Override
    void read(BsonStream stream) {
        setValue(stream.getINT32());
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString(){
        return Integer.toString(value);
    }

}
