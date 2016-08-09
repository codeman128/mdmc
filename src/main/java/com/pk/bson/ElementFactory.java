package com.pk.bson;

/**
 * Created by PavelK on 8/5/2016.
 */
public class ElementFactory {

    static public Element createElement(BSON.TYPE type, MutableString name){
        switch (type){
            case STRING   : return new StringElement(name);
            case INT32    : return new INT32Element(name);
            case EMBEDDED : return new Document(name);
            case ARRAY    : return new Array(name);
            default       : return null;
        }
    }
}
