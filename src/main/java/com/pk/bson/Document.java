package com.pk.bson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class Document extends Element{
    private final Map<ElementName, Element> elements = new HashMap<>();
    private final ElementName locator = new ElementName(200); //200 should be good enough

    Document(ElementName name) {
        super(name);
    }

    private Map<ElementName, Element> getElements(){
        return elements;
    }

    private Element getElement(byte[] name, int nameOffset, int nameLength) {
        locator.setName(name, nameOffset, nameLength);
        return elements.get(locator);
    }

    private Element readElement(BSON.TYPE type, BsonStream stream){
        int nameLength = stream.readKey();
        Element e = getElement(stream.getBuffer(), 0, nameLength);
        if (e==null) {
            ElementName elementName = new ElementName(stream.getBuffer(), 0, nameLength);
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
        return 0;
    }




}
