package com.pk.bson;

/**
 * Created by pkapovski on 8/4/2016.
 */
public abstract class Element {
    private MutableString name;

    private Element(){}

    Element(MutableString name) {
        this.name = name;
    }

    public MutableString getName(){
        return name;
    }

    abstract void read(BsonStream stream);

}
