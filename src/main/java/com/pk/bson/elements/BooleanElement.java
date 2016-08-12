package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.MutableString;

/**
 * Created by PavelK on 8/12/2016.
 */
public class BooleanElement extends Element{
    boolean value;

    BooleanElement(MutableString name) {
        super(name);
    }

    @Override
    protected void read(BsonStream stream) {
        setValue(stream.readBoolean());
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String toString(){
        return Boolean.toString(value);
    }

}
