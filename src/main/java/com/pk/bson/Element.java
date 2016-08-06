package com.pk.bson;

/**
 * Created by pkapovski on 8/4/2016.
 */
public abstract class Element {
    private ElementName name;

    private Element(){}

    Element(ElementName name) {
        this.name = name;
    }

    public ElementName getName(){
        return name;
    }

    abstract void read(BsonStream stream);

}
