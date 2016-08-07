package com.pk.bson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class Document extends Element{
    private final Map<MutableString, Element> elements = new HashMap<>();
    private final MutableString locator = new MutableString(200); //200 should be good enough

    Document(MutableString name) {
        super(name);
    }

    private Map<MutableString, Element> getElements(){
        return elements;
    }

    private Element getElement(byte[] name, int nameOffset, int nameLength) {
        locator.set(name, nameOffset, nameLength);
        return elements.get(locator);
    }

    private Element readElement(BSON.TYPE type, BsonStream stream){
        int nameLength = stream.readKey();
        Element e = getElement(stream.getBuffer(), 0, nameLength);
        if (e==null) {
            MutableString elementName = new MutableString(stream.getBuffer(), 0, nameLength);
            e = ElementFactory.createElement(type, elementName);
            elements.put(elementName, e);
        }
        e.read(stream);
        return e;
    }

    void read(BsonStream stream) {
        int size = stream.getINT32();
        BSON.TYPE type;
        while(true) {
            type = stream.getType();
            switch (type) {
                case EOO: return;
                case STRING:
                case INT32:
                case EMBEDDED: {
                    readElement(type, stream);
                    break;
                }
            }
        }
    }

    public int getInt32(MutableString mStr){
        INT32Element e = (INT32Element)elements.get(mStr);
        if (e!=null) {
            return e.getValue();
        } else {
            return 0;
        }
    }




}
