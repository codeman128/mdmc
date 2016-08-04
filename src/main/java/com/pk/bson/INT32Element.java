package com.pk.bson;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class INT32Element extends Element {
    int value;

    INT32Element(ElementName name, int value) {
        super(name);
        this.value = value;
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
