package com.pk.bson;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class StringElement extends Element {
    private MutableString value = new MutableString(200);

    StringElement(MutableString name) {
        super(name);
    }

    @Override
    void read(BsonStream stream) {
        stream.readString(value);
    }

    public MutableString getValue() {
        return value;
    }

    public String toString(){
        return "\""+value.toString()+"\"";
    }

    public void setValue(MutableString value) {
        this.value.copyFrom(value);
    }
}
