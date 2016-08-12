package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.MutableString;

/**
 * Created by PavelK on 8/12/2016.
 */
public class DoubleElement extends Element{
    double value;

    DoubleElement(MutableString name) {
        super(name);
    }

    @Override
    protected void read(BsonStream stream) {
        setValue(stream.getDouble());
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String toString(){
        return Double.toString(value);
    }

}
